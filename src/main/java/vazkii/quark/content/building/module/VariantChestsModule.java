package vazkii.quark.content.building.module;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.StructureBlockReplacementHandler;
import vazkii.quark.base.handler.StructureBlockReplacementHandler.StructureHolder;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
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

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

@LoadModule(category = ModuleCategory.BUILDING, hasSubscriptions = true, antiOverlap = { "woodworks" })
public class VariantChestsModule extends QuarkModule {

	private static final String DONK_CHEST = "Quark:DonkChest";

	private static final ImmutableSet<Wood> VANILLA_WOODS = ImmutableSet.copyOf(VanillaWoods.ALL);
	private static final ImmutableSet<String> MOD_WOODS = ImmutableSet.of();

	public static BlockEntityType<VariantChestBlockEntity> chestTEType;
	public static BlockEntityType<VariantTrappedChestBlockEntity> trappedChestTEType;

	private static final List<ChestInfo> chestTypes = new LinkedList<>();

	public static final List<Block> chests = new LinkedList<>();
	public static final List<Block> trappedChests = new LinkedList<>();

	private static final List<Block> allChests = new LinkedList<>();
	private static final Map<ResourceLocation, Block> chestMappings = new HashMap<>();
	private static final Map<ResourceLocation, Block> trappedChestMappings = new HashMap<>();

	@Config
	private static boolean replaceWorldgenChests = true;
	@Config(flag = "chest_reversion")
	private static boolean enableRevertingWoodenChests = true;

	private static boolean staticEnabled = false;

	@Config(description = "Chests to put in each structure. The format per entry is \"structure=chest\", where \"structure\" is a structure ID, and \"chest\" is a block ID, which must correspond to a standard chest block.")
	public static List<String> structureChests = Arrays.asList(
		"minecraft:village_plains=quark:oak_chest",
		"minecraft:igloo=quark:spruce_chest",
		"minecraft:village_snowy=quark:spruce_chest",
		"minecraft:village_taiga=quark:spruce_chest",
		"minecraft:desert_pyramid=quark:birch_chest",
		"minecraft:jungle_pyramid=quark:jungle_chest",
		"minecraft:village_desert=quark:jungle_chest",
		"minecraft:village_savanna=quark:acacia_chest",
		"minecraft:mansion=quark:dark_oak_chest",
		"minecraft:pillager_outpost=quark:dark_oak_chest",
		"minecraft:ruined_portal=quark:crimson_chest",
		"minecraft:ruined_portal_desert=quark:crimson_chest",
		"minecraft:ruined_portal_jungle=quark:crimson_chest",
		"minecraft:ruined_portal_mountain=quark:crimson_chest",
		"minecraft:ruined_portal_nether=quark:crimson_chest",
		"minecraft:ruined_portal_ocean=quark:crimson_chest",
		"minecraft:ruined_portal_swamp=quark:crimson_chest",
		"minecraft:bastion_remnant=quark:crimson_chest",
		"minecraft:fortress=quark:nether_brick_chest",
		"minecraft:end_city=quark:purpur_chest",
		"minecraft:ocean_ruin_cold=quark:prismarine_chest",
		"minecraft:ocean_ruin_warm=quark:prismarine_chest",
		"minecraft:ancient_city=quark:ancient_chest");

	private static final List<String> BUILT_IN_MOD_STRUCTURES = Arrays.asList(
		"bettermineshafts:mineshaft=quark:oak_chest",
		"betterstrongholds:stronghold=quark:oak_chest",
		"cobbler:shulker_factory=quark:purpur_chest",
		"conjurer_illager:theatre=quark:dark_oak_chest",
		"dungeoncrawl:dungeon=quark:oak_chest",
		"dungeons_plus:bigger_dungeon=quark:oak_chest",
		"dungeons_plus:end_ruins=quark:purpur_chest",
		"dungeons_plus:leviathan=quark:jungle_chest",
		"dungeons_plus:snowy_temple=quark:spruce_chest",
		"dungeons_plus:soul_prison=quark:warped_chest",
		"dungeons_plus:tower=quark:oak_chest",
		"dungeons_plus:warped_garden=quark:warped_chest",
		"hunterillager:hunterhouse=quark:spruce_chest",
		"iceandfire:gorgon_temple=quark:jungle_chest",
		"illagers_plus:illager_archer_tower=quark:dark_oak_chest",
		"illagers_plus:illager_centre=quark:dark_oak_chest",
		"illagers_plus:illager_fort=quark:dark_oak_chest",
		"illagers_plus:illager_tower=quark:dark_oak_chest",
		"infernalexp:bastion_outpost=quark:crimson_chest",
		"infernalexp:glowstone_canyon_ruin=quark:crimson_chest",
		"infernalexp:strider_altar=quark:crimson_chest",
		"pandoras_creatures:end_prison=quark:purpur_chest",
		"mowziesmobs:barakoa_village=quark:acacia_chest",
		"endreborn:end_crypt=quark:purpur_chest",
		"endreborn:end_shipwreck=quark:purpur_chest",
		"majruszs_difficulty:flying_end_ship=quark:purpur_chest",
		"majruszs_difficulty:flying_phantom_structure=quark:purpur_chest",
		"outer_end:catacombs=quark:purpur_chest",
		"outer_end:end_tower=quark:purpur_chest",
		"stoneholm:underground_village=quark:spruce_chest",
		"repurposed_structures:ancient_city_end=quark:purpur_chest",
		"repurposed_structures:ancient_city_nether=quark:nether_brick_chest",
		"repurposed_structures:ancient_city_ocean=quark:prismarine_chest",
		"repurposed_structures:bastion_underground=quark:dark_oak_chest",
		"repurposed_structures:city_nether=quark:nether_brick_chest",
		"repurposed_structures:city_overworld=quark:oak_chest",
		"repurposed_structures:fortress_jungle=quark:jungle_chest",
		"repurposed_structures:igloo_grassy=quark:oak_chest",
		"repurposed_structures:igloo_mangrove=quark:mangrove_chest",
		"repurposed_structures:igloo_mushroom=quark:blossom_chest",
		"repurposed_structures:igloo_stone=quark:spruce_chest",
		"repurposed_structures:mansion_birch=quark:birch_chest",
		"repurposed_structures:mansion_desert=quark:jungle_chest",
		"repurposed_structures:mansion_jungle=quark:jungle_chest",
		"repurposed_structures:mansion_mangrove=quark:mangrove_chest",
		"repurposed_structures:mansion_oak=quark:oak_chest",
		"repurposed_structures:mansion_savanna=quark:acacia_chest",
		"repurposed_structures:mansion_snowy=quark:spruce_chest",
		"repurposed_structures:mansion_taiga=quark:spruce_chest",
		"repurposed_structures:mineshaft_birch=quark:birch_chest",
		"repurposed_structures:mineshaft_crimson=quark:crimson_chest",
		"repurposed_structures:mineshaft_dark_forest=quark:dark_oak_chest",
		"repurposed_structures:mineshaft_desert=quark:jungle_chest",
		"repurposed_structures:mineshaft_end=quark:purpur_chest",
		"repurposed_structures:mineshaft_icy=quark:spruce_chest",
		"repurposed_structures:mineshaft_jungle=quark:jungle_chest",
		"repurposed_structures:mineshaft_nether=quark:nether_brick_chest",
		"repurposed_structures:mineshaft_ocean=quark:prismarine_chest",
		"repurposed_structures:mineshaft_savanna=quark:acacia_chest",
		"repurposed_structures:mineshaft_stone=quark:spruce_chest",
		"repurposed_structures:mineshaft_swamp=quark:dark_oak_chest",
		"repurposed_structures:mineshaft_taiga=quark:spruce_chest",
		"repurposed_structures:mineshaft_warped=quark:warped_chest",
		"repurposed_structures:monument_desert=quark:jungle_chest",
		"repurposed_structures:monument_icy=quark:spruce_chest",
		"repurposed_structures:monument_jungle=quark:jungle_chest",
		"repurposed_structures:monument_nether=quark:nether_brick_chest",
		"repurposed_structures:outpost_badlands=quark:dark_oak_chest",
		"repurposed_structures:outpost_birch=quark:birch_chest",
		"repurposed_structures:outpost_crimson=quark:crimson_chest",
		"repurposed_structures:outpost_desert=quark:jungle_chest",
		"repurposed_structures:outpost_giant_tree_taiga=quark:spruce_chest",
		"repurposed_structures:outpost_icy=quark:spruce_chest",
		"repurposed_structures:outpost_jungle=quark:jungle_chest",
		"repurposed_structures:outpost_mangrove=quark:mangrove_chest",
		"repurposed_structures:outpost_nether_brick=quark:nether_brick_chest",
		"repurposed_structures:outpost_oak=quark:oak_chest",
		"repurposed_structures:outpost_snowy=quark:spruce_chest",
		"repurposed_structures:outpost_taiga=quark:spruce_chest",
		"repurposed_structures:outpost_warped=quark:warped_chest",
		"repurposed_structures:pyramid_badlands=quark:dark_oak_chest",
		"repurposed_structures:pyramid_dark_forest=quark:dark_oak_chest",
		"repurposed_structures:pyramid_end=quark:purpur_chest",
		"repurposed_structures:pyramid_flower_forest=quark:blossom_chest",
		"repurposed_structures:pyramid_giant_tree_taiga=quark:spruce_chest",
		"repurposed_structures:pyramid_icy=quark:spruce_chest",
		"repurposed_structures:pyramid_jungle=quark:jungle_chest",
		"repurposed_structures:pyramid_mushroom=quark:blossom_chest",
		"repurposed_structures:pyramid_nether=quark:nether_brick_chest",
		"repurposed_structures:pyramid_ocean=quark:prismarine_chest",
		"repurposed_structures:pyramid_snowy=quark:spruce_chest",
		"repurposed_structures:ruined_portal_end=quark:purpur_chest",
		"repurposed_structures:ruins_land_cold=quark:oak_chest",
		"repurposed_structures:ruins_land_hot=quark:jungle_chest",
		"repurposed_structures:ruins_land_icy=quark:spruce_chest",
		"repurposed_structures:ruins_land_warm=quark:oak_chest",
		"repurposed_structures:ruins_nether=quark:nether_brick_chest",
		"repurposed_structures:shipwreck_crimson=quark:crimson_chest",
		"repurposed_structures:shipwreck_end=quark:purpur_chest",
		"repurposed_structures:shipwreck_nether_bricks=quark:nether_brick_chest",
		"repurposed_structures:shipwreck_warped=quark:warped_chest",
		"repurposed_structures:stronghold_nether=quark:nether_brick_chest",
		"repurposed_structures:stronghold_end=quark:purpur_chest",
		"repurposed_structures:temple_nether_basalt=quark:nether_brick_chest",
		"repurposed_structures:temple_nether_crimson=quark:crimson_chest",
		"repurposed_structures:temple_nether_soul=quark:warped_chest",
		"repurposed_structures:temple_nether_warped=quark:warped_chest",
		"repurposed_structures:temple_nether_wasteland=quark:nether_brick_chest",
		"repurposed_structures:temple_ocean=quark:prismarine_chest",
		"repurposed_structures:temple_taiga=quark:spruce_chest",
		"repurposed_structures:village_badlands=quark:dark_oak_chest",
		"repurposed_structures:village_birch=quark:birch_chest",
		"repurposed_structures:village_crimson=quark:crimson_chest",
		"repurposed_structures:village_dark_forest=quark:dark_oak_chest",
		"repurposed_structures:village_giant_taiga=quark:spruce_chest",
		"repurposed_structures:village_jungle=quark:jungle_chest",
		"repurposed_structures:village_mountains=quark:spruce_chest",
		"repurposed_structures:village_mushroom=quark:blossom_chest",
		"repurposed_structures:village_oak=quark:oak_chest",
		"repurposed_structures:village_ocean=quark:spruce_chest",
		"repurposed_structures:village_swamp=quark:azalea_chest",
		"repurposed_structures:village_warped=quark:warped_chest",
		"repurposed_structures:witch_hut_birch=quark:birch_chest",
		"repurposed_structures:witch_hut_dark_forest=quark:dark_oak_chest",
		"repurposed_structures:witch_hut_giant_tree_taiga=quark:spruce_chest",
		"repurposed_structures:witch_hut_mangrove=quark:mangrove_chest",
		"repurposed_structures:witch_hut_oak=quark:oak_chest",
		"repurposed_structures:witch_hut_taiga=quark:spruce_chest",
		"valhelsia_structures:big_tree=quark:oak_chest",
		"valhelsia_structures:castle=quark:spruce_chest",
		"valhelsia_structures:castle_ruin=quark:oak_chest",
		"valhelsia_structures:desert_house=quark:spruce_chest",
		"valhelsia_structures:forge=quark:spruce_chest",
		"valhelsia_structures:player_house=quark:oak_chest",
		"valhelsia_structures:small_castle=quark:oak_chest",
		"valhelsia_structures:small_dungeon=quark:oak_chest",
		"valhelsia_structures:spawner_dungeon=quark:oak_chest",
		"valhelsia_structures:tower_ruin=quark:spruce_chest",
		"valhelsia_structures:witch_hut=quark:spruce_chest");

	@Override
	public void register() {
		ForgeRegistries.RECIPE_SERIALIZERS.register(Quark.MOD_ID + ":mixed_exclusion", MixedExclusionRecipe.SERIALIZER);

		VANILLA_WOODS.forEach(s -> addChest(s.name(), Blocks.CHEST));
		MOD_WOODS.forEach(s -> addModChest(s, Blocks.CHEST));

		addChest("nether_brick", Blocks.NETHER_BRICKS);
		addChest("purpur", Blocks.PURPUR_BLOCK);
		addChest("prismarine", Blocks.PRISMARINE);

		StructureBlockReplacementHandler.addReplacement(VariantChestsModule::getGenerationChestBlockState);
	}

	@Override
	public void postRegister() {
		chestTEType = registerChests(VariantChestBlockEntity::new, () -> chestTEType,
			VariantChestBlock::new, VariantChestBlock.Compat::new,
			allChests::addAll, chests::addAll);
		trappedChestTEType = registerChests(VariantTrappedChestBlockEntity::new, () -> trappedChestTEType,
			VariantTrappedChestBlock::new, VariantTrappedChestBlock.Compat::new,
			allChests::addAll, trappedChests::addAll);

		RegistryHelper.register(chestTEType, "variant_chest", Registry.BLOCK_ENTITY_TYPE_REGISTRY);
		RegistryHelper.register(trappedChestTEType, "variant_trapped_chest", Registry.BLOCK_ENTITY_TYPE_REGISTRY);

		ILootrIntegration.INSTANCE.postRegister();
	}

	@Override
	public void loadComplete() {
		ILootrIntegration.INSTANCE.loadComplete();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		BlockEntityRenderers.register(chestTEType, VariantChestRenderer::new);
		BlockEntityRenderers.register(trappedChestTEType, VariantChestRenderer::new);

		ILootrIntegration.INSTANCE.clientSetup();
	}

	@Override
	public void configChanged() {
		super.configChanged();

		staticEnabled = enabled;

		chestMappings.clear();
		trappedChestMappings.clear();
		List<String> chestsClone = new ArrayList<>(BUILT_IN_MOD_STRUCTURES);
		chestsClone.addAll(structureChests);

		for (String s : chestsClone) {
			String[] toks = s.split("=");
			if (toks.length == 2) {
				String left = toks[0];
				String right = toks[1];

				Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(right));
				if (block != null && block != Blocks.AIR) {
					chestMappings.put(new ResourceLocation(left), block);
					if (chests.contains(block)) {
						var trapped = trappedChests.get(chests.indexOf(block));
						trappedChestMappings.put(new ResourceLocation(left), trapped);
					}
				}
			}
		}
	}

	private static BlockState getGenerationChestBlockState(ServerLevelAccessor accessor, BlockState current, StructureHolder structure) {
		if (staticEnabled && replaceWorldgenChests) {
			if (current.getBlock() == Blocks.CHEST) {
				ResourceLocation res = StructureBlockReplacementHandler.getStructureRes(accessor, structure);

				if (res != null && chestMappings.containsKey(res)) {
					Block block = chestMappings.get(res);
					return block.withPropertiesOf(current);
				}
			} else if (current.getBlock() == Blocks.TRAPPED_CHEST) {
				ResourceLocation res = StructureBlockReplacementHandler.getStructureRes(accessor, structure);

				if (res != null && trappedChestMappings.containsKey(res)) {
					Block block = trappedChestMappings.get(res);
					return block.withPropertiesOf(current);
				}
			}
		}

		return null; // no change
	}

	private void addChest(String name, Block from) {
		addChest(name, () -> Block.Properties.copy(from));
	}

	public void addChest(String name, Supplier<Block.Properties> props) {
		addChest(name, this, props, false);
	}

	public static void addChest(String name, QuarkModule module, Supplier<Block.Properties> props, boolean external) {
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
																			Consumer<List<Block>>... toStitch) {
		List<Block> blockTypes = chestTypes.stream().map(it -> {
			Block block = it.mod != null ?
				compatType.createChest(it.type, it.mod, it.module, tileType, it.props.get()) :
				chestType.createChest(it.type, it.module, tileType, it.props.get());

			if (it.condition != null && block instanceof IQuarkBlock quarkBlock)
				quarkBlock.setCondition(it.condition);

			return block;
		}).toList();

		for (var consumer : toStitch)
			consumer.accept(blockTypes);

		return BlockEntityType.Builder.<T>of(factory, blockTypes.toArray(new Block[0])).build(null);
	}

	@Override
	public void textureStitch(TextureStitchEvent.Pre event) {
		if (event.getAtlas().location().toString().equals("minecraft:textures/atlas/chest.png")) {
			for (Block b : allChests)
				VariantChestRenderer.accept(event, b);
			ILootrIntegration.INSTANCE.stitch(event);
		}
	}

	@SubscribeEvent
	public void onClickEntity(PlayerInteractEvent.EntityInteractSpecific event) {
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

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {
		Entity target = event.getEntity();
		if (target instanceof AbstractChestedHorse horse) {
			ItemStack chest = ItemStack.of(horse.getPersistentData().getCompound(DONK_CHEST));
			if (!chest.isEmpty() && horse.hasChest())
				WAIT_TO_REPLACE_CHEST.set(chest);
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinLevelEvent event) {
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
							 QuarkModule module,
							 Supplier<BlockBehaviour.Properties> props,
							 @Nullable BooleanSupplier condition,
							 @Nullable String mod) {
	}

	@FunctionalInterface
	public interface ChestConstructor {
		Block createChest(String type, QuarkModule module, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, BlockBehaviour.Properties props);
	}

	@FunctionalInterface
	public interface CompatChestConstructor {
		Block createChest(String type, String mod, QuarkModule module, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, BlockBehaviour.Properties props);
	}
}
