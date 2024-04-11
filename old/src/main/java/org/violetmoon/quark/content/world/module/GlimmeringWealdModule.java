package org.violetmoon.quark.content.world.module;

import com.google.common.collect.ImmutableSet;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.config.QuarkGeneralConfig;
import org.violetmoon.quark.base.handler.QuarkSounds;
import org.violetmoon.quark.content.mobs.module.StonelingsModule;
import org.violetmoon.quark.content.world.block.GlowLichenGrowthBlock;
import org.violetmoon.quark.content.world.block.GlowShroomBlock;
import org.violetmoon.quark.content.world.block.GlowShroomRingBlock;
import org.violetmoon.quark.content.world.block.HugeGlowShroomBlock;
import org.violetmoon.quark.content.world.feature.GlowExtrasFeature;
import org.violetmoon.quark.content.world.feature.GlowShroomsFeature;
import org.violetmoon.zeta.advancement.modifier.AdventuringTimeModifier;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;
import org.violetmoon.zeta.util.Hint;

import java.util.List;

@ZetaLoadModule(category = "world")
public class GlimmeringWealdModule extends ZetaModule {

	public static final ResourceLocation BIOME_NAME = new ResourceLocation(Quark.MOD_ID, "glimmering_weald");
	public static final ResourceKey<Biome> BIOME_KEY = ResourceKey.create(Registries.BIOME, BIOME_NAME);

	public static GlowShroomsFeature glow_shrooms_feature;
	public static GlowExtrasFeature glow_shrooms_extra_feature;

	public static Holder<PlacedFeature> ore_lapis_extra;
	public static Holder<PlacedFeature> placed_glow_shrooms;
	public static Holder<PlacedFeature> placed_glow_extras;

	@Hint
	public static Block glow_shroom;
	@Hint
	public static Block glow_lichen_growth;
	public static Block glow_shroom_block;
	public static Block glow_shroom_stem;
	public static Block glow_shroom_ring;

	public static TagKey<Item> glowShroomFeedablesTag;

	@Config(
		name = "Min Depth Range",
		description = "Experimental, dont change if you dont know what you are doing. Depth min value from which biome will spawn. Decreasing will make biome appear more often"
	)
	@Config.Min(-2)
	@Config.Max(2)
	public static double minDepthRange = 1.55F;
	@Config(
		name = "Max Weirdness Range",
		description = "Experimental, dont change if you dont know what you are doing. Depth max value until which biome will spawn. Increasing will make biome appear more often"
	)
	@Config.Min(-2)
	@Config.Max(2)
	public static double maxDepthRange = 2;

	@LoadEvent
	public final void register(ZRegister event) {
		CreativeTabManager.daisyChain();
		glow_shroom = new GlowShroomBlock(this).setCreativeTab(CreativeModeTabs.NATURAL_BLOCKS, Blocks.HANGING_ROOTS, false);
		glow_shroom_block = new HugeGlowShroomBlock("glow_shroom_block", this, true);
		glow_shroom_stem = new HugeGlowShroomBlock("glow_shroom_stem", this, false);
		glow_shroom_ring = new GlowShroomRingBlock(this);
		glow_lichen_growth = new GlowLichenGrowthBlock(this);
		CreativeTabManager.endDaisyChain();


		event.getVariantRegistry().addFlowerPot(glow_lichen_growth, "glow_lichen_growth", prop -> prop.lightLevel((state) -> 8));
		event.getVariantRegistry().addFlowerPot(glow_shroom, "glow_shroom", prop -> prop.lightLevel((state) -> 10));


		// Feature
		glow_shrooms_feature= new GlowShroomsFeature();
		event.getRegistry().register(glow_shrooms_feature, "glow_shrooms", Registries.FEATURE);
		glow_shrooms_extra_feature = new GlowExtrasFeature();
		event.getRegistry().register(glow_shrooms_extra_feature, "glow_shrooms_extras", Registries.FEATURE);


	}

	@LoadEvent
	public void postRegister(ZRegister.Post e) {
		float wmin = (float) minDepthRange;
		float wmax = (float) maxDepthRange;
		if(wmin >= wmax) {
			Quark.LOG.warn("Incorrect value for Glimmering Weald biome parameters. Using default");
			wmax = 2;
			wmin = 1.55f;
		}
		Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
		Quark.TERRABLENDER_INTEGRATION.registerUndergroundBiome(this, BIOME_NAME, Climate.parameters(FULL_RANGE, FULL_RANGE, FULL_RANGE, FULL_RANGE,
				Climate.Parameter.span(wmin, wmax), FULL_RANGE, 0F));

		Quark.ZETA.advancementModifierRegistry.addModifier(new AdventuringTimeModifier(this, ImmutableSet.of(BIOME_KEY)));
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
}
