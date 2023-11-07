package vazkii.quark.content.mobs.module;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.EntityAttributeHandler;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.handler.advancement.mod.TwoByTwoModifier;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.base.module.config.type.EntitySpawnConfig;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.mobs.client.render.entity.ShibaRenderer;
import vazkii.quark.content.mobs.entity.Shiba;
import vazkii.zeta.client.event.ZClientSetup;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

@ZetaLoadModule(category = "mobs")
public class ShibaModule extends ZetaModule {

	public static EntityType<Shiba> shibaType;

	@Config
	public static EntitySpawnConfig spawnConfig = new EntitySpawnConfig(40, 1, 3, CompoundBiomeConfig.fromBiomeTags(false, BiomeTags.IS_MOUNTAIN));

	@Config public static boolean ignoreAreasWithSkylight = false;

	@Hint(key = "shiba_find_low_light") Item torch = Items.TORCH;

	public static QuarkGenericTrigger shibaHelpTrigger;

	@LoadEvent
	public final void register(ZRegister event) {
		shibaType = EntityType.Builder.of(Shiba::new, MobCategory.CREATURE)
				.sized(0.8F, 0.8F)
				.clientTrackingRange(8)
				.setCustomClientFactory((spawnEntity, world) -> new Shiba(shibaType, world))
				.build("shiba");
		Quark.ZETA.registry.register(shibaType, "shiba", Registry.ENTITY_TYPE_REGISTRY);

		EntitySpawnHandler.registerSpawn(shibaType, MobCategory.CREATURE, Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, spawnConfig);
		EntitySpawnHandler.addEgg(this, shibaType, 0xa86741, 0xe8d5b6, spawnConfig);

		EntityAttributeHandler.put(shibaType, Wolf::createAttributes);

		QuarkAdvancementHandler.addModifier(new TwoByTwoModifier(this, ImmutableSet.of(shibaType)));

		shibaHelpTrigger = QuarkAdvancementHandler.registerGenericTrigger("shiba_help");
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		EntityRenderers.register(shibaType, ShibaRenderer::new);
	}

}
