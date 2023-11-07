package vazkii.quark.content.mobs.module;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraftforge.common.Tags;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.EntityAttributeHandler;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.module.config.type.EntitySpawnConfig;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.mobs.client.render.entity.ToretoiseRenderer;
import vazkii.quark.content.mobs.entity.Toretoise;
import vazkii.zeta.client.event.ZClientSetup;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

import java.util.List;

@ZetaLoadModule(category = "mobs")
public class ToretoiseModule extends ZetaModule {

	public static EntityType<Toretoise> toretoiseType;

	@Config public static int maxYLevel = 0;

	@Config(description="The number of ticks from mining a tortoise until feeding it could cause it to regrow.")
	public static int cooldownTicks = 20 * 60;

	@Config(description="The items that can be fed to toretoises to make them regrow ores.")
	public static List<String> foods = Lists.newArrayList("minecraft:glow_berries");

	@Config(flag = "toretoise_regrow")
	public static boolean allowToretoiseToRegrow = true;
	
	@Config(description="Feeding a toretoise after cooldown will regrow them with a one-in-this-number chance. "
			+ "Set to 1 to always regrow, higher = lower chance.")
	public static int regrowChance = 3;

	@Config
	public static DimensionConfig dimensions = DimensionConfig.overworld(false);

	@Config
	public static EntitySpawnConfig spawnConfig = new EntitySpawnConfig(120, 2, 4, CompoundBiomeConfig.fromBiomeTags(true, Tags.Biomes.IS_VOID, BiomeTags.IS_NETHER, BiomeTags.IS_END));

	public static QuarkGenericTrigger mineToretoiseTrigger;
	public static QuarkGenericTrigger mineFedToretoiseTrigger;
	
	@LoadEvent
	public final void register(ZRegister event) {
		toretoiseType = EntityType.Builder.of(Toretoise::new, MobCategory.CREATURE)
				.sized(2F, 1F)
				.clientTrackingRange(8)
				.fireImmune()
				.setCustomClientFactory((spawnEntity, world) -> new Toretoise(toretoiseType, world))
				.build("toretoise");

		Quark.ZETA.registry.register(toretoiseType, "toretoise", Registry.ENTITY_TYPE_REGISTRY);

		EntitySpawnHandler.registerSpawn(toretoiseType, MobCategory.MONSTER, Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Toretoise::spawnPredicate, spawnConfig);
		EntitySpawnHandler.addEgg(this, toretoiseType, 0x55413b, 0x383237, spawnConfig);

		EntityAttributeHandler.put(toretoiseType, Toretoise::prepareAttributes);
		
		mineToretoiseTrigger = QuarkAdvancementHandler.registerGenericTrigger("mine_toretoise");
		mineFedToretoiseTrigger = QuarkAdvancementHandler.registerGenericTrigger("mine_fed_toretoise");
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		EntityRenderers.register(toretoiseType, ToretoiseRenderer::new);
	}

}
