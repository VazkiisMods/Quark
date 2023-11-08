package vazkii.quark.content.world.module;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.handler.UndergroundBiomeHandler;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.mod.AdventuringTimeModifier;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.mobs.module.StonelingsModule;
import vazkii.quark.content.world.block.GlowLichenGrowthBlock;
import vazkii.quark.content.world.block.GlowShroomBlock;
import vazkii.quark.content.world.block.GlowShroomRingBlock;
import vazkii.quark.content.world.block.HugeGlowShroomBlock;
import vazkii.quark.content.world.feature.GlowExtrasFeature;
import vazkii.quark.content.world.feature.GlowShroomsFeature;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

import java.util.List;

@ZetaLoadModule(category = "world")
public class GlimmeringWealdModule extends ZetaModule {

	private static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
	public static final ResourceLocation BIOME_NAME = new ResourceLocation(Quark.MOD_ID, "glimmering_weald");
	public static final ResourceKey<Biome> BIOME_KEY = ResourceKey.create(Registry.BIOME_REGISTRY, BIOME_NAME);
	
	public static final Holder<PlacedFeature> ORE_LAPIS_EXTRA = PlacementUtils.register("ore_lapis_glimmering_weald", OreFeatures.ORE_LAPIS, OrePlacements.commonOrePlacement(12, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(0))));
	public static Holder<PlacedFeature> placed_glow_shrooms;
	public static Holder<PlacedFeature> placed_glow_extras;

	@Hint public static Block glow_shroom;
	@Hint public static Block glow_lichen_growth;
	public static Block glow_shroom_block;
	public static Block glow_shroom_stem;
	public static Block glow_shroom_ring;

	public static TagKey<Item> glowShroomFeedablesTag;

	@Config(name = "Min Depth Range",
			description = "Experimental, dont change if you dont know what you are doing. Depth min value from which biome will spawn. Decreasing will make biome appear more often")
	@Config.Min(-2)
	@Config.Max(2)
	public static double minDepthRange = 1.55F;
	@Config(name = "Max Weirdness Range",
			description = "Experimental, dont change if you dont know what you are doing. Depth max value until which biome will spawn. Increasing will make biome appear more often")
	@Config.Min(-2)
	@Config.Max(2)
	public static double maxDepthRange = 2;

	@LoadEvent
	public final void register(ZRegister event) {
		glow_shroom = new GlowShroomBlock(this);
		glow_lichen_growth = new GlowLichenGrowthBlock(this);
		glow_shroom_block = new HugeGlowShroomBlock("glow_shroom_block", this, true);
		glow_shroom_stem = new HugeGlowShroomBlock("glow_shroom_stem", this, false);
		glow_shroom_ring = new GlowShroomRingBlock(this);

		VariantHandler.addFlowerPot(glow_lichen_growth, "glow_lichen_growth", prop -> prop.lightLevel((state) -> 8));
		VariantHandler.addFlowerPot(glow_shroom, "glow_shroom", prop -> prop.lightLevel((state) -> 10));

		makeFeatures();


	}

	@LoadEvent
	public void postRegister(ZRegister.Post e) {
		Quark.ZETA.registry.register(makeBiome(), BIOME_NAME, Registry.BIOME_REGISTRY);
		float wmin = (float) minDepthRange;
		float wmax = (float) maxDepthRange;
		if(wmin >= wmax){
			Quark.LOG.warn("Incorrect value for Glimmering Weald biome parameters. Using default");
			wmax = 2;
			wmin = 1.55f;
		}
		UndergroundBiomeHandler.addUndergroundBiome(this, Climate.parameters(FULL_RANGE, FULL_RANGE, FULL_RANGE, FULL_RANGE,
				Climate.Parameter.span(wmin, wmax), FULL_RANGE, 0F), BIOME_NAME);

		QuarkAdvancementHandler.addModifier(new AdventuringTimeModifier(this, ImmutableSet.of(BIOME_KEY)));
	}

	@LoadEvent
	public void setup(ZCommonSetup e) {
		glowShroomFeedablesTag = ItemTags.create(new ResourceLocation(Quark.MOD_ID, "glow_shroom_feedables"));

		e.enqueueWork(() -> {
			ComposterBlock.COMPOSTABLES.put(glow_shroom.asItem(), 0.65F);
			ComposterBlock.COMPOSTABLES.put(glow_shroom_block.asItem(), 0.65F);
			ComposterBlock.COMPOSTABLES.put(glow_shroom_stem.asItem(), 0.65F);
			ComposterBlock.COMPOSTABLES.put(glow_shroom_ring.asItem(), 0.65F);

			ComposterBlock.COMPOSTABLES.put(glow_lichen_growth.asItem(), 0.5F);
		});
	}

	private static void makeFeatures() {
		placed_glow_shrooms = place("glow_shrooms", new GlowShroomsFeature(), GlowShroomsFeature.placed());
		placed_glow_extras = place("glow_extras", new GlowExtrasFeature(), GlowExtrasFeature.placed());
	}

	private static Holder<PlacedFeature> place(String featureName, Feature<NoneFeatureConfiguration> feature, List<PlacementModifier> placer) {
		String name = Quark.MOD_ID + ":" + featureName;

		Quark.ZETA.registry.register(feature, name, Registry.FEATURE_REGISTRY);
		Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> configured = FeatureUtils.register(name, feature, NoneFeatureConfiguration.NONE);
		return PlacementUtils.register(name, configured, placer);
	}

	private static Biome makeBiome() {
		MobSpawnSettings.Builder mobs = new MobSpawnSettings.Builder();
		BiomeDefaultFeatures.commonSpawns(mobs);

		if(ModuleLoader.INSTANCE.isModuleEnabled(StonelingsModule.class))
			mobs.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(StonelingsModule.stonelingType, 200, 1, 4));
		mobs.addSpawn(MobCategory.UNDERGROUND_WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GLOW_SQUID, 20, 4, 6));

		BiomeGenerationSettings.Builder settings = new BiomeGenerationSettings.Builder();
		OverworldBiomes.globalOverworldGeneration(settings);
		BiomeDefaultFeatures.addPlainGrass(settings);
		BiomeDefaultFeatures.addDefaultOres(settings, true);
		BiomeDefaultFeatures.addDefaultSoftDisks(settings);
		BiomeDefaultFeatures.addPlainVegetation(settings);
		BiomeDefaultFeatures.addDefaultMushrooms(settings);
		BiomeDefaultFeatures.addDefaultExtraVegetation(settings);

		settings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, placed_glow_shrooms);
		settings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, placed_glow_extras);

		settings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_LAPIS_EXTRA);

		Music music = Musics.createGameMusic(QuarkSounds.MUSIC_GLIMMERING_WEALD);
		Biome biome = OverworldBiomes.biome(Biome.Precipitation.RAIN, 0.8F, 0.4F, mobs, settings, music);

		return biome;
	}

}
