package vazkii.quark.content.mobs.module;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.EntityAttributeHandler;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.module.config.type.EntitySpawnConfig;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.mobs.client.render.entity.StonelingRenderer;
import vazkii.quark.content.mobs.entity.Stoneling;
import vazkii.quark.content.mobs.item.DiamondHeartItem;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZClientSetup;

@LoadModule(category = "mobs", hasSubscriptions = true)
public class StonelingsModule extends QuarkModule {

	public static EntityType<Stoneling> stonelingType;

	@Config
	public static int maxYLevel = 0;
	@Config
	public static DimensionConfig dimensions = DimensionConfig.overworld(false);
	@Config
	public static EntitySpawnConfig spawnConfig = new EntitySpawnConfig(80, 1, 1, CompoundBiomeConfig.fromBiomeTags(true, Tags.Biomes.IS_VOID, BiomeTags.IS_NETHER, BiomeTags.IS_END));
	@Config(flag = "stoneling_drop_diamond_heart")
	public static boolean enableDiamondHeart = true;
	@Config(description = "When enabled, stonelings are much more aggressive in checking for players")
	public static boolean cautiousStonelings = false;
	@Config
	public static boolean tamableStonelings = true;

	@Config(description = "Disabled if if Pathfinder Maps are disabled.", flag = "stoneling_weald_pathfinder")
	public static boolean wealdPathfinderMaps = true;

	public static QuarkGenericTrigger makeStonelingTrigger;
	
	@Hint("stoneling_drop_diamond_heart") public static Item diamondHeart;

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		makeStonelingTrigger = QuarkAdvancementHandler.registerGenericTrigger("make_stoneling");
	}
	
	public boolean registered = false;

	@LoadEvent
	public final void register(ZRegister event) {
		this.registered = true;
		diamondHeart = new DiamondHeartItem("diamond_heart", this, new Item.Properties().tab(CreativeModeTab.TAB_MISC));

		stonelingType = EntityType.Builder.of(Stoneling::new, MobCategory.CREATURE)
				.sized(0.5F, 0.9F)
				.clientTrackingRange(8)
				.setCustomClientFactory((spawnEntity, world) -> new Stoneling(stonelingType, world))
				.build("stoneling");
		Quark.ZETA.registry.register(stonelingType, "stoneling", Registry.ENTITY_TYPE_REGISTRY);

		EntitySpawnHandler.registerSpawn(stonelingType, MobCategory.MONSTER, Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Stoneling::spawnPredicate, spawnConfig);
		EntitySpawnHandler.addEgg(this, stonelingType, 0xA1A1A1, 0x505050, spawnConfig);

		EntityAttributeHandler.put(stonelingType, Stoneling::prepareAttributes);
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		EntityRenderers.register(stonelingType, StonelingRenderer::new);
	}

}
