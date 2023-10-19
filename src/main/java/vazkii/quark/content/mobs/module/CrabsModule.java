package vazkii.quark.content.mobs.module;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.BrewingHandler;
import vazkii.quark.base.handler.EntityAttributeHandler;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.mod.BalancedDietModifier;
import vazkii.quark.base.handler.advancement.mod.FuriousCocktailModifier;
import vazkii.quark.base.handler.advancement.mod.TwoByTwoModifier;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.item.QuarkMobBucketItem;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.base.module.config.type.EntitySpawnConfig;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.base.util.QuarkEffect;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.mobs.client.render.entity.CrabRenderer;
import vazkii.quark.content.mobs.entity.Crab;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZClientSetup;

/**
 * @author WireSegal
 * Created at 7:28 PM on 9/22/19.
 */
@LoadModule(category = "mobs", hasSubscriptions = true)
public class CrabsModule extends QuarkModule {

	public static EntityType<Crab> crabType;

	@Config
	public static EntitySpawnConfig spawnConfig = new EntitySpawnConfig(5, 1, 3, CompoundBiomeConfig.fromBiomeTags(false, BiomeTags.IS_BEACH));

	public static TagKey<Block> crabSpawnableTag;
	public static MobEffect resilience;

	@Config(flag = "crab_brewing")
	public static boolean enableBrewing = true;

	@Config(description = "Whether Resilience should be required for 'How Did We Get Here?' and (if brewing is enabled) 'A Furious Cocktail'.\n" +
		"Keep this on when brewing is disabled if your pack adds an alternative source for the effect.")
	public static boolean resilienceRequiredForAllEffects = true;

	@Hint(key = "crab_info") Item crab_leg;
	@Hint(key = "crab_info") Item crab_shell;
	@Hint(key = "crab_info") public static Item crab_bucket;

	@LoadEvent
	public final void register(ZRegister event) {
		crab_leg = new QuarkItem("crab_leg", this, new Item.Properties()
				.tab(CreativeModeTab.TAB_FOOD)
				.food(new FoodProperties.Builder()
						.meat()
						.nutrition(1)
						.saturationMod(0.3F)
						.build()));

		Item cookedCrabLeg = new QuarkItem("cooked_crab_leg", this, new Item.Properties()
				.tab(CreativeModeTab.TAB_FOOD)
				.food(new FoodProperties.Builder()
						.meat()
						.nutrition(8)
						.saturationMod(0.8F)
						.build()));

		crab_shell = new QuarkItem("crab_shell", this, new Item.Properties().tab(CreativeModeTab.TAB_BREWING))
				.setCondition(() -> enableBrewing);

		crab_bucket = new QuarkMobBucketItem(() -> crabType, () -> Fluids.WATER, () -> QuarkSounds.BUCKET_EMPTY_CRAB, "crab_bucket", this);

		resilience = new QuarkEffect("resilience", MobEffectCategory.BENEFICIAL, 0x5b1a04);
		resilience.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "2ddf3f0a-f386-47b6-aeb0-6bd32851f215", 0.5, AttributeModifier.Operation.ADDITION);

		BrewingHandler.addPotionMix("crab_brewing", () -> Ingredient.of(crab_shell), resilience);

		crabType = EntityType.Builder.<Crab>of(Crab::new, MobCategory.CREATURE)
				.sized(0.9F, 0.5F)
				.clientTrackingRange(8)
				.setCustomClientFactory((spawnEntity, world) -> new Crab(crabType, world))
				.build("crab");
		Quark.ZETA.registry.register(crabType, "crab", Registry.ENTITY_TYPE_REGISTRY);

		EntitySpawnHandler.registerSpawn(crabType, MobCategory.CREATURE, Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Crab::spawnPredicate, spawnConfig);
		EntitySpawnHandler.addEgg(this, crabType, 0x893c22, 0x916548, spawnConfig);

		EntityAttributeHandler.put(crabType, Crab::prepareAttributes);

		QuarkAdvancementHandler.addModifier(new FuriousCocktailModifier(this, () -> enableBrewing, ImmutableSet.of(resilience))
				.setCondition(() -> resilienceRequiredForAllEffects));
		QuarkAdvancementHandler.addModifier(new TwoByTwoModifier(this, ImmutableSet.of(crabType)));
		QuarkAdvancementHandler.addModifier(new BalancedDietModifier(this, ImmutableSet.of(crab_leg, cookedCrabLeg)));
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		crabSpawnableTag = BlockTags.create(new ResourceLocation(Quark.MOD_ID, "crab_spawnable"));
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		EntityRenderers.register(crabType, CrabRenderer::new);
	}
}
