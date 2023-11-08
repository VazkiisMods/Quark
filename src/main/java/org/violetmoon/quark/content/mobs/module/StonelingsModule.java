package org.violetmoon.quark.content.mobs.module;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.config.type.CompoundBiomeConfig;
import org.violetmoon.quark.base.config.type.DimensionConfig;
import org.violetmoon.quark.base.config.type.EntitySpawnConfig;
import org.violetmoon.quark.base.handler.EntityAttributeHandler;
import org.violetmoon.quark.base.handler.advancement.QuarkAdvancementHandler;
import org.violetmoon.quark.base.handler.advancement.QuarkGenericTrigger;
import org.violetmoon.quark.base.world.EntitySpawnHandler;
import org.violetmoon.quark.content.mobs.client.render.entity.StonelingRenderer;
import org.violetmoon.quark.content.mobs.entity.Stoneling;
import org.violetmoon.quark.content.mobs.item.DiamondHeartItem;
import org.violetmoon.zeta.client.event.load.ZClientSetup;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraftforge.common.Tags;

@ZetaLoadModule(category = "mobs")
public class StonelingsModule extends ZetaModule {

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
