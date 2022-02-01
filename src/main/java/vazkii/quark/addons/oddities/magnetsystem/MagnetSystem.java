package vazkii.quark.addons.oddities.magnetsystem;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import vazkii.quark.addons.oddities.block.be.MagnetBlockEntity;
import vazkii.quark.addons.oddities.module.MagnetsModule;
import vazkii.quark.api.IMagnetMoveAction;
import vazkii.quark.api.IMagnetTracker;
import vazkii.quark.api.QuarkCapabilities;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.ModuleLoader;

@EventBusSubscriber(bus = Bus.FORGE, modid = Quark.MOD_ID)
public class MagnetSystem {
	
	private static HashSet<Block> magnetizableBlocks = new HashSet<>();
	
	private static final HashMap<Block, IMagnetMoveAction> BLOCK_MOVE_ACTIONS = new HashMap<>();
	
	static {
		DefaultMoveActions.addActions(BLOCK_MOVE_ACTIONS);
	}

	public static IMagnetMoveAction getMoveAction(Block block) {
		return BLOCK_MOVE_ACTIONS.get(block);
	}

    public static LazyOptional<IMagnetTracker> getCapability(Level world) {
        return world.getCapability(QuarkCapabilities.MAGNET_TRACKER_CAPABILITY);
    }
	
	@SubscribeEvent
	public static void tick(WorldTickEvent event) {
		if(!ModuleLoader.INSTANCE.isModuleEnabled(MagnetsModule.class))
			return;
		
		if (event.phase == Phase.START) {
			getCapability(event.world).ifPresent(IMagnetTracker::clear);
		} else {
			if (magnetizableBlocks.isEmpty())
				loadMagnetizableBlocks(event.world);
			getCapability(event.world).ifPresent(magnetTracker -> {
				for (BlockPos pos : magnetTracker.getTrackedPositions())
					magnetTracker.actOnForces(pos);
				magnetTracker.clear();
			});
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void tick(ClientTickEvent event) {
		if(!ModuleLoader.INSTANCE.isModuleEnabled(MagnetsModule.class))
			return;
		
		if (Minecraft.getInstance().level == null) {
			magnetizableBlocks.clear();
		}
	}


	public static void applyForce(Level world, BlockPos pos, int magnitude, boolean pushing, Direction dir, int distance, BlockPos origin) {
		getCapability(world).ifPresent(magnetTracker ->
				magnetTracker.applyForce(pos, magnitude, pushing, dir, distance, origin));
	}
	
	public static PushReaction getPushAction(MagnetBlockEntity magnet, BlockPos pos, BlockState state, Direction moveDir) {
		Level world = magnet.getLevel();
		if(world != null && isBlockMagnetic(state)) {
			BlockPos targetLocation = pos.relative(moveDir);
			BlockState stateAtTarget = world.getBlockState(targetLocation);
			if (stateAtTarget.isAir())
				return PushReaction.IGNORE;
			else if (stateAtTarget.getPistonPushReaction() == PushReaction.DESTROY)
				return PushReaction.DESTROY;
		}

		return PushReaction.BLOCK;
	}
	
	public static boolean isBlockMagnetic(BlockState state) {
		Block block = state.getBlock();

		if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
			if (state.getValue(PistonBaseBlock.EXTENDED))
				return false;
		}
		
		return block != MagnetsModule.magnet && (magnetizableBlocks.contains(block) || BLOCK_MOVE_ACTIONS.containsKey(block) || block instanceof IMagnetMoveAction);
	}
	
	private static void loadMagnetizableBlocks(Level world) {
		RecipeManager manager = world.getRecipeManager();
		if(!manager.getRecipes().isEmpty()) {
			Collection<Recipe<?>> recipes = manager.getRecipes();

			Multimap<Item, Item> recipeDigestion = HashMultimap.create();

			for(Recipe<?> recipe : recipes) {
				if(recipe == null || recipe.getResultItem() == null || recipe.getIngredients() == null)
					continue;
				
				Item out = recipe.getResultItem().getItem();

				NonNullList<Ingredient> ingredients = recipe.getIngredients();
				for(Ingredient ingredient : ingredients) {
					for (ItemStack inStack : ingredient.getItems())
						recipeDigestion.put(inStack.getItem(), out);
				}
			}


			List<Item> magneticDerivationList = MiscUtil.massRegistryGet(MagnetsModule.magneticDerivationList, Registry.ITEM);
			List<Item> magneticWhitelist = MiscUtil.massRegistryGet(MagnetsModule.magneticWhitelist, Registry.ITEM);
			List<Item> magneticBlacklist = MiscUtil.massRegistryGet(MagnetsModule.magneticBlacklist, Registry.ITEM);
			
			Streams.concat(magneticDerivationList.stream(), magneticWhitelist.stream())
				.filter(i -> i instanceof BlockItem)
				.map(i -> ((BlockItem) i).getBlock())
				.forEach(magnetizableBlocks::add);
			
			Set<Item> scanned = Sets.newHashSet(magneticDerivationList);
			List<Item> magnetizableToScan = Lists.newArrayList(magneticDerivationList);

			while (!magnetizableToScan.isEmpty()) {
				Item scan = magnetizableToScan.remove(0);

				if (recipeDigestion.containsKey(scan)) {
					for (Item candidate : recipeDigestion.get(scan)) {
						if (!scanned.contains(candidate)) {
							scanned.add(candidate);
							magnetizableToScan.add(candidate);

							if(candidate instanceof BlockItem && !magneticBlacklist.contains(candidate))
								magnetizableBlocks.add(((BlockItem) candidate).getBlock());
						}
					}
				}
			}
		}
	}
}
