package vazkii.quark.world.module;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Potions;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.effect.QuarkEffect;
import vazkii.quark.base.handler.BrewingHandler;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.Config;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.recipe.FlagIngredient;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.base.world.config.BiomeTypeConfig;
import vazkii.quark.base.world.config.ConditionalEntitySpawnConfig;
import vazkii.quark.world.client.render.CrabRenderer;
import vazkii.quark.world.client.render.FrogRenderer;
import vazkii.quark.world.entity.CrabEntity;
import vazkii.quark.world.entity.FrogEntity;

/**
 * @author WireSegal
 * Created at 7:28 PM on 9/22/19.
 */
@LoadModule(category = ModuleCategory.WORLD, hasSubscriptions = true)
public class PassiveCreaturesModule extends Module {
	public static EntityType<FrogEntity> frogType;
	public static EntityType<CrabEntity> crabType;

	@Config(name = "frogs")
	public static ConditionalEntitySpawnConfig frogConfig = new ConditionalEntitySpawnConfig("frogs", 40, 1, 3, new BiomeTypeConfig(false, BiomeDictionary.Type.SWAMP));

	@Config(name = "crabs")
	public static ConditionalEntitySpawnConfig crabConfig = new ConditionalEntitySpawnConfig("crabs", 5, 1, 3, new BiomeTypeConfig(false, BiomeDictionary.Type.BEACH));

	@Config(flag = "passive_creatures_brewing")
	public static boolean enableBrewing = true;

	@Config
	public static boolean enableJokes = false;

	@Override
	public void construct() {
		new QuarkItem("frog_leg", this, new Item.Properties()
				.group(ItemGroup.FOOD)
				.food(new Food.Builder()
						.meat()
						.hunger(2)
						.saturation(0.3F)
						.build()))
		.setCondition(() -> frogConfig.enabled);

		new QuarkItem("cooked_frog_leg", this, new Item.Properties()
				.group(ItemGroup.FOOD)
				.food(new Food.Builder()
						.meat()
						.hunger(4)
						.saturation(1.25F)
						.build()))
		.setCondition(() -> frogConfig.enabled);

		Item goldenLeg = new QuarkItem("golden_frog_leg", this, new Item.Properties()
				.group(ItemGroup.BREWING)
				.food(new Food.Builder()
						.meat()
						.hunger(4)
						.saturation(2.5F)
						.build()))
				.setCondition(() -> frogConfig.enabled && enableBrewing);

		new QuarkItem("crab_leg", this, new Item.Properties()
				.group(ItemGroup.FOOD)
				.food(new Food.Builder()
						.meat()
						.hunger(1)
						.saturation(0.3F)
						.build()))
		.setCondition(() -> crabConfig.enabled);

		new QuarkItem("cooked_crab_leg", this, new Item.Properties()
				.group(ItemGroup.FOOD)
				.food(new Food.Builder()
						.meat()
						.hunger(8)
						.saturation(0.8F)
						.build()))
		.setCondition(() -> crabConfig.enabled);

		Item shell = new QuarkItem("crab_shell", this, new Item.Properties().group(ItemGroup.BREWING))
				.setCondition(() -> crabConfig.enabled && enableBrewing);

		Effect resilience = new QuarkEffect("resilience", EffectType.BENEFICIAL, 0x5b1a04);
		resilience.addAttributesModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, "2ddf3f0a-f386-47b6-aeb0-6bd32851f215", 0.5, AttributeModifier.Operation.ADDITION);

		BrewingHandler.addPotionMix("passive_creatures_brewing",
				() -> new FlagIngredient(Ingredient.fromItems(goldenLeg), "frogs"),
				Potions.LEAPING, Potions.LONG_LEAPING, Potions.STRONG_LEAPING);

		BrewingHandler.addPotionMix("passive_creatures_brewing",
				() -> new FlagIngredient(Ingredient.fromItems(shell), "crabs"), resilience);

		frogType = EntityType.Builder.<FrogEntity>create(FrogEntity::new, EntityClassification.CREATURE)
				.size(0.65F, 0.5F)
				.setTrackingRange(80)
				.setUpdateInterval(3)
				.setShouldReceiveVelocityUpdates(true)
				.setCustomClientFactory((spawnEntity, world) -> new FrogEntity(frogType, world))
				.build("frog");
		RegistryHelper.register(frogType, "frog");
		EntitySpawnHandler.registerSpawn(this, frogType, EntityClassification.CREATURE, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canAnimalSpawn, frogConfig);
		EntitySpawnHandler.addEgg(frogType, 0xbc9869, 0xffe6ad, frogConfig);

		crabType = EntityType.Builder.<CrabEntity>create(CrabEntity::new, EntityClassification.CREATURE)
				.size(0.9F, 0.5F)
				.setTrackingRange(80)
				.setUpdateInterval(3)
				.setShouldReceiveVelocityUpdates(true)
				.setCustomClientFactory((spawnEntity, world) -> new CrabEntity(crabType, world))
				.build("crab");
		RegistryHelper.register(crabType, "crab");
		EntitySpawnHandler.registerSpawn(this, crabType, EntityClassification.CREATURE, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, CrabEntity::spawnPredicate, crabConfig);
		EntitySpawnHandler.addEgg(crabType, 0x893c22, 0x916548, crabConfig);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		RenderingRegistry.registerEntityRenderingHandler(frogType, FrogRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(crabType, CrabRenderer::new);
	}
}
