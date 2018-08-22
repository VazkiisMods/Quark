package vazkii.quark.decoration.feature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.arl.client.RetexturedModel;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockColoredFlowerPot;

public class ColoredFlowerPots extends Feature {

	public static BlockColoredFlowerPot[] pots;
	public static boolean enableComparatorLogic;
	public static String[] overrides;
	private static Map<Pair<Item,Integer>,Integer> flowers = new HashMap<>();
	private static boolean loadedConfig = false;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		pots = new BlockColoredFlowerPot[EnumDyeColor.values().length];
		for(int i = 0; i < pots.length; i++) {
			pots[i] = new BlockColoredFlowerPot(EnumDyeColor.byMetadata(i));
			RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(pots[i]), ProxyRegistry.newStack(Items.FLOWER_POT), LibMisc.OREDICT_DYES.get(15 - i));
		}
	}

	@Override
	public void setupConfig() {
		enableComparatorLogic = loadPropBool("Comparator Logic", "If true, filled flower pots will respond to comparators based on the contents.", true);
		overrides = loadPropStringList("Flower Overrides", "List of itemstacks to override default flower behavior, default checks for BlockBush.\n"
				+ "Format is 'modid:name[:meta]->power'. Unset meta will default wildcard. Power refers to comparator power, non-zero makes it valid for a flower pot. Specific values:\n"
				+ "* 0 - not flower, blacklists from placing in a flower pot\n* 1 - mushroom\n* 4 - fern\n* 7 - flower\n* 10 - dead bush\n* 12 - sapling\n* 15 - cactus", new String[] {
				"biomesoplenty:mushroom->1",
				"biomesoplenty:flower_0->7",
				"biomesoplenty:flower_0->7",
				"biomesoplenty:sapling_0->12",
				"biomesoplenty:sapling_1->12",
				"biomesoplenty:sapling_2->12",
				"quark:glowshroom->1" // does not extend BlockMushroom, so comparator power is guessed wrongly
		});
	}

	@Override
	public void init(FMLInitializationEvent event) {
		loadFlowersFromConfig();
	}


	/* utils */

	public static void loadFlowersFromConfig() {
		// prevent loading twice as this method is called by both flower pot types
		if(loadedConfig) {
			return;
		}
		loadedConfig = true;

		// add vanilla plants that don't fit the normal rules
		registerFlower(new ItemStack(Blocks.CACTUS), 15);
		registerFlower(new ItemStack(Blocks.TALLGRASS, 1, BlockTallGrass.EnumType.FERN.getMeta()), 4);

		// parse config
		for(String line : overrides) {
			String[] split = line.split("->");
			if(split.length != 2) {
				FMLLog.log.error("Invalid Quark flower pot override, expected format 'modid:name[:meta]->power'");
				continue;
			}

			String[] itemData = split[0].split(":");
			if(itemData.length < 2 || itemData.length > 3) {
				FMLLog.log.error("Invalid Quark flower pot override, expected format 'modid:name[:meta]->power'");
				continue;
			}

			// parse comparator power
			int power;
			try {
				power = Integer.parseInt(split[1]);
			} catch(NumberFormatException e) {
				power = -1;
			}
			if(power < 0 || power > 15) {
				FMLLog.log.error("Invalid Quark flower pot override, power must be a valid number from 0 to 15");
				continue;
			}

			// find item
			ResourceLocation location = new ResourceLocation(itemData[0], itemData[1]);
			Item item = GameRegistry.findRegistry(Item.class).getValue(location);
			if(item == null || item == Items.AIR) {
				FMLLog.log.debug("Unable to find item {} for Quark flower override", location.toString());
				continue;
			}

			// if length is 3, we have meta
			if(itemData.length == 3) {
				int meta;
				try {
					meta = Integer.parseInt(itemData[2]);
				} catch(NumberFormatException e) {
					meta = -1;
				}
				if(meta < 0) {
					FMLLog.log.error("Invalid Quark flower pot override, meta must be a valid positive number");
					continue;
				}
				registerFlower(new ItemStack(item, 1, meta), power);
			} else {
				// if no meta, assume wildcard
				NonNullList<ItemStack> subItems = NonNullList.create();
				item.getSubItems(CreativeTabs.SEARCH, subItems);
				for(ItemStack stack : subItems) {
					registerFlower(stack, power);
				}
			}
		}
	}

	/**
	 * Checks if the given item stack is a flower
	 * @param stack  Input stack
	 * @return  True if its a valid flower
	 */
	public static boolean isFlower(ItemStack stack) {
		return getFlowerComparatorPower(stack) > 0;
	}

	public static int getFlowerComparatorPower(ItemStack stack) {
		return flowers.computeIfAbsent(Pair.of(stack.getItem(), stack.getMetadata()), (key) -> {
			Block block = Block.getBlockFromItem(key.getLeft());

			// not a flower means 0 override
			// this handles vanilla logic excluding cactuses and ferns, which are registered directly above
			if(!(block instanceof BlockBush)
					|| block instanceof BlockDoublePlant
					|| block instanceof BlockTallGrass
					|| block instanceof BlockCrops
					|| block instanceof BlockLilyPad) {
				return 0;
			}

			// cactus: 15
			if(block instanceof BlockSapling) {
				return 12;
			}
			if(block instanceof BlockDeadBush) {
				return 10;
			}
			// fern: 4
			if(block instanceof BlockMushroom) {
				return 1;
			}

			// flowers mostly
			return 7;
		});
	}

	/**
	 * Registers an override to state a stack is definitely a flower or not a flower, primarily used by the config
	 * @param stack     ItemStack which is a flower
	 * @param power     Comparator level. Set to 0 to register this as not a flower
	 */
	public static void registerFlower(ItemStack stack, int power) {
		flowers.put(Pair.of(stack.getItem(), stack.getMetadata()), power);
	}
	/** Any items which have blockColors methods that throw an exception */
	private static Set<Item> unsafeBlockColors = new HashSet<>();

	/**
	 * Gets the block colors for a block from an itemstack, logging an exception if it fails. Use this to get block colors when the implementation is unknown
	 * @param stack  Stack to use
	 * @param world  World
	 * @param pos    Pos
	 * @param index  Tint index
	 * @return  color, or -1 for undefined
	 */
	public static int getStackBlockColorsSafe(ItemStack stack, @Nullable IBlockAccess world, @Nullable BlockPos pos, int index) {
		if(stack.isEmpty()) {
			return -1;
		}

		// do not try if it failed before
		Item item = stack.getItem();
		if(!unsafeBlockColors.contains(item)) {
			try {
				return getStackBlockColors(stack, world, pos, index);
			} catch (Exception e) {
				// catch and log possible exceptions. Most likely exception is ClassCastException if they do not perform safety checks
				new RuntimeException(item.getRegistryName() + " errored on getting flower pot block colors", e).printStackTrace();
				unsafeBlockColors.add(item);
			}
		}

		// fallback to item colors
		return Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, index);
	}

	/**
	 * Gets the block colors from an item stack
	 * @param stack  Stack to check
	 * @param world  World
	 * @param pos    Pos
	 * @param index  Tint index
	 * @return  color, or -1 for undefined
	 */
	@SuppressWarnings("deprecation")
	public static int getStackBlockColors(ItemStack stack, @Nullable IBlockAccess world, @Nullable BlockPos pos, int index) {
		if(stack.isEmpty() || !(stack.getItem() instanceof ItemBlock)) {
			return -1;
		}
		ItemBlock item = (ItemBlock) stack.getItem();
		IBlockState iblockstate = item.getBlock().getStateFromMeta(item.getMetadata(stack));
		return Minecraft.getMinecraft().getBlockColors().colorMultiplier(iblockstate, world, pos, index);
	}


	/* events */

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		for(EnumDyeColor color : EnumDyeColor.values()) {
			ModelResourceLocation location = new ModelResourceLocation(pots[color.getMetadata()].getRegistryName(), "contents=custom");
			IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Error loading model for " + location);
			IBakedModel standard = event.getModelRegistry().getObject(location);
			IBakedModel finalModel = new RetexturedModel(standard, model, DefaultVertexFormats.BLOCK, "plant");
			event.getModelRegistry().putObject(location, finalModel);
		}
	}
}
