package vazkii.quark.content.building.module;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.Tags;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.StructureBlockReplacementHandler;
import vazkii.quark.base.handler.StructureBlockReplacementHandler.StructureHolder;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.util.VanillaWoods;
import vazkii.quark.base.util.VanillaWoods.Wood;
import vazkii.quark.content.building.block.VariantChestBlock;
import vazkii.quark.content.building.block.VariantTrappedChestBlock;
import vazkii.quark.content.building.block.be.VariantChestBlockEntity;
import vazkii.quark.content.building.block.be.VariantTrappedChestBlockEntity;
import vazkii.quark.content.building.client.render.be.VariantChestRenderer;
import vazkii.quark.content.building.recipe.MixedExclusionRecipe;
import vazkii.quark.integration.lootr.ILootrIntegration;
import vazkii.quark.mixin.accessor.AccessorAbstractChestedHorse;
import vazkii.zeta.client.event.ZClientSetup;
import vazkii.zeta.client.event.ZPreTextureStitch;
import vazkii.zeta.event.*;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.IDisableable;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ZetaLoadModule(category = "building", antiOverlap = { "woodworks" })
public class VariantChestsModule extends ZetaModule {

	private static final String DONK_CHEST = "Quark:DonkChest";

	private static final ImmutableSet<Wood> VANILLA_WOODS = ImmutableSet.copyOf(VanillaWoods.ALL);
	private static final ImmutableSet<String> MOD_WOODS = ImmutableSet.of();

	public static BlockEntityType<VariantChestBlockEntity> chestTEType;
	public static BlockEntityType<VariantTrappedChestBlockEntity> trappedChestTEType;

	private static final List<ChestInfo> chestTypes = new LinkedList<>();
	private static final Map<ChestInfo, TagKey<Structure>> structureTags = new HashMap<>();

	public static final List<Block> chests = new LinkedList<>();
	public static final List<Block> trappedChests = new LinkedList<>();

	private static final List<Block> allChests = new LinkedList<>();
	private static final Map<ResourceLocation, Block> manualChestMappings = new HashMap<>();
	private static final Map<ResourceLocation, Block> manualTrappedChestMappings = new HashMap<>();
	private static final Map<TagKey<Structure>, Block> chestMappings = new HashMap<>();
	private static final Map<TagKey<Structure>, Block> trappedChestMappings = new HashMap<>();

	@Config
	private static boolean replaceWorldgenChests = true;
	@Config(flag = "chest_reversion")
	private static boolean enableRevertingWoodenChests = true;

	private static boolean staticEnabled = false;

	@Config(description = "Chests to put in structures. It's preferred to use worldgen tags for this. The format per entry is \"structure=chest\", where \"structure\" is a structure ID, and \"chest\" is a block ID, which must correspond to a standard chest block.")
	public static List<String> structureChests = new ArrayList<>();

	@LoadEvent
	public final void register(ZRegister event) {
		event.getRegistry().register(MixedExclusionRecipe.SERIALIZER, "mixed_exclusion", Registry.RECIPE_SERIALIZER_REGISTRY);

		VANILLA_WOODS.forEach(s -> addChest(s.name(), Blocks.CHEST));
		MOD_WOODS.forEach(s -> addModChest(s, Blocks.CHEST));

		addChest("nether_brick", Blocks.NETHER_BRICKS);
		addChest("purpur", Blocks.PURPUR_BLOCK);
		addChest("prismarine", Blocks.PRISMARINE);

		StructureBlockReplacementHandler.addReplacement(VariantChestsModule::getGenerationChestBlockState);
	}

	@LoadEvent
	public void postRegister(ZRegister.Post e) {
		chestTEType = registerChests(VariantChestBlockEntity::new, () -> chestTEType,
			VariantChestBlock::new, VariantChestBlock.Compat::new, chestMappings,
			allChests::addAll, chests::addAll);
		trappedChestTEType = registerChests(VariantTrappedChestBlockEntity::new, () -> trappedChestTEType,
			VariantTrappedChestBlock::new, VariantTrappedChestBlock.Compat::new, trappedChestMappings,
			allChests::addAll, trappedChests::addAll);

		Quark.ZETA.registry.register(chestTEType, "variant_chest", Registry.BLOCK_ENTITY_TYPE_REGISTRY);
		Quark.ZETA.registry.register(trappedChestTEType, "variant_trapped_chest", Registry.BLOCK_ENTITY_TYPE_REGISTRY);

		ILootrIntegration.INSTANCE.postRegister();
	}

	@LoadEvent
	public void loadComplete(ZLoadComplete e) {
		ILootrIntegration.INSTANCE.loadComplete();
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		BlockEntityRenderers.register(chestTEType, VariantChestRenderer::new);
		BlockEntityRenderers.register(trappedChestTEType, VariantChestRenderer::new);

		ILootrIntegration.INSTANCE.clientSetup();
	}

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		staticEnabled = enabled;

		manualChestMappings.clear();
		manualTrappedChestMappings.clear();
		List<String> chestsClone = new ArrayList<>(structureChests);

		for (String s : chestsClone) {
			String[] toks = s.split("=");
			if (toks.length == 2) {
				String left = toks[0];
				String right = toks[1];

				Block block = Registry.BLOCK.get(new ResourceLocation(right));
				if (block != Blocks.AIR) {
					manualChestMappings.put(new ResourceLocation(left), block);
					if (chests.contains(block)) {
						var trapped = trappedChests.get(chests.indexOf(block));
						manualTrappedChestMappings.put(new ResourceLocation(left), trapped);
					}
				}
			}
		}
	}

	private static BlockState getGenerationChestBlockState(ServerLevelAccessor accessor, BlockState current, StructureHolder structure) {
		if (staticEnabled && replaceWorldgenChests) {
			if (current.getBlock() == Blocks.CHEST) {
				return replaceChestState(accessor, current, structure, chestMappings, manualChestMappings);
			} else if (current.getBlock() == Blocks.TRAPPED_CHEST) {
				return replaceChestState(accessor, current, structure, trappedChestMappings, manualTrappedChestMappings);
			}
		}

		return null; // no change
	}

	@Nullable
	private static BlockState replaceChestState(ServerLevelAccessor accessor, BlockState current, StructureHolder structure, Map<TagKey<Structure>, Block> mappings, Map<ResourceLocation, Block> manualMappings) {
		Holder<Structure> structureHolder = StructureBlockReplacementHandler.getStructure(accessor, structure);
		if (structureHolder != null) {
			for (TagKey<Structure> structureTagKey : mappings.keySet()) {
				if (structureHolder.is(structureTagKey)) {
					Block block = mappings.get(structureTagKey);
					return block.withPropertiesOf(current);
				}
			}

			Optional<Block> manualMapping = structureHolder.unwrapKey().map(ResourceKey::location).map(manualMappings::get);
			if (manualMapping.isPresent())
				return manualMapping.get().withPropertiesOf(current);
		}


		return null; // no change
	}

	private void addChest(String name, Block from) {
		addChest(name, () -> Block.Properties.copy(from));
	}

	public void addChest(String name, Supplier<Block.Properties> props) {
		addChest(name, this, props, false);
	}

	public static void addChest(String name, ZetaModule module, Supplier<Block.Properties> props, boolean external) {
		BooleanSupplier cond = external ? (() -> ModuleLoader.INSTANCE.isModuleEnabled(VariantChestsModule.class)) : (() -> true);

		chestTypes.add(new ChestInfo(name, module, props, cond, null));
	}

	private void addModChest(String nameRaw, Block from) {
		String[] toks = nameRaw.split(":");
		String name = toks[1];
		String mod = toks[0];
		addModChest(name, mod, () -> Block.Properties.copy(from));
	}

	private void addModChest(String name, String mod, Supplier<Block.Properties> props) {
		chestTypes.add(new ChestInfo(name, this, props, null, mod));
	}

	@SafeVarargs
	public static <T extends BlockEntity> BlockEntityType<T> registerChests(BlockEntitySupplier<? extends T> factory,
																			Supplier<BlockEntityType<? extends ChestBlockEntity>> tileType,
																			ChestConstructor chestType, CompatChestConstructor compatType,
																			@Nullable Map<TagKey<Structure>, Block> structureMappings,
																			Consumer<List<Block>>... toStitch) {
		List<Block> blockTypes = chestTypes.stream().map(it -> {
			Block block = it.mod != null ?
				compatType.createChest(it.type, it.mod, it.module, tileType, it.props.get()) :
				chestType.createChest(it.type, it.module, tileType, it.props.get());

			if (it.condition != null && block instanceof IDisableable<?> disableable)
				disableable.setCondition(it.condition);

			if (structureMappings != null) {
				TagKey<Structure> tag = structureTags.computeIfAbsent(it, (info) -> TagKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(Quark.MOD_ID, info.type + "_chest_structures")));
				structureMappings.put(tag, block);
			}

			return block;
		}).toList();

		for (var consumer : toStitch)
			consumer.accept(blockTypes);

		return BlockEntityType.Builder.<T>of(factory, blockTypes.toArray(new Block[0])).build(null);
	}

	@LoadEvent
	public void textureStitch(ZPreTextureStitch event) {
		if (event.getAtlas().location().toString().equals("minecraft:textures/atlas/chest.png")) {
			for (Block b : allChests)
				VariantChestRenderer.accept(event, b);
			ILootrIntegration.INSTANCE.stitch(event);
		}
	}

	@PlayEvent
	public void onClickEntity(ZPlayerInteract.EntityInteractSpecific event) {
		Entity target = event.getTarget();
		Player player = event.getEntity();
		ItemStack held = player.getItemInHand(event.getHand());

		if (!held.isEmpty() && target instanceof AbstractChestedHorse horse) {

			if (!horse.hasChest() && held.getItem() != Items.CHEST) {
				if (held.is(Tags.Items.CHESTS_WOODEN)) {
					event.setCanceled(true);
					event.setCancellationResult(InteractionResult.SUCCESS);

					if (!target.level.isClientSide) {
						ItemStack copy = held.copy();
						copy.setCount(1);
						held.shrink(1);

						horse.getPersistentData().put(DONK_CHEST, copy.serializeNBT());

						horse.setChest(true);
						horse.createInventory();
						((AccessorAbstractChestedHorse) horse).quark$playChestEquipsSound();
					}
				}
			}
		}
	}

	private static final ThreadLocal<ItemStack> WAIT_TO_REPLACE_CHEST = new ThreadLocal<>();

	@PlayEvent
	public void onDeath(ZLivingDeath event) {
		Entity target = event.getEntity();
		if (target instanceof AbstractChestedHorse horse) {
			ItemStack chest = ItemStack.of(horse.getPersistentData().getCompound(DONK_CHEST));
			if (!chest.isEmpty() && horse.hasChest())
				WAIT_TO_REPLACE_CHEST.set(chest);
		}
	}

	@PlayEvent
	public void onEntityJoinWorld(ZEntityJoinLevel event) {
		Entity target = event.getEntity();
		if (target instanceof ItemEntity item && item.getItem().getItem() == Items.CHEST) {
			ItemStack local = WAIT_TO_REPLACE_CHEST.get();
			if (local != null && !local.isEmpty())
				((ItemEntity) target).setItem(local);
			WAIT_TO_REPLACE_CHEST.remove();
		}
	}

	public interface IChestTextureProvider {
		String getChestTexturePath();

		boolean isTrap();
	}

	private record ChestInfo(String type,
							 ZetaModule module,
							 Supplier<BlockBehaviour.Properties> props,
							 @Nullable BooleanSupplier condition,
							 @Nullable String mod) {

	}

	@FunctionalInterface
	public interface ChestConstructor {
		Block createChest(String type, ZetaModule module, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, BlockBehaviour.Properties props);
	}

	@FunctionalInterface
	public interface CompatChestConstructor {
		Block createChest(String type, String mod, ZetaModule module, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, BlockBehaviour.Properties props);
	}
}
