package vazkii.quark.mobs.module;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.item.Items;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.Config;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.base.world.config.BiomeTypeConfig;
import vazkii.quark.base.world.config.DimensionConfig;
import vazkii.quark.base.world.config.EntitySpawnConfig;
import vazkii.quark.mobs.client.render.ToretoiseRenderer;
import vazkii.quark.mobs.entity.StonelingEntity;
import vazkii.quark.mobs.entity.ToretoiseEntity;

@LoadModule(category = ModuleCategory.MOBS, hasSubscriptions = true)
public class ToretoiseModule extends Module {

	public static EntityType<ToretoiseEntity> toretoiseType;
	
	@Config public static boolean disableIronFarms = false;
	@Config public static int maxYLevel = 32;
	
	@Config
	public static DimensionConfig dimensions = DimensionConfig.overworld(false);
	
	@Config 
	public static EntitySpawnConfig spawnConfig = new EntitySpawnConfig(40, 1, 1, new BiomeTypeConfig(true, BiomeDictionary.Type.VOID));
	
	@Override
	public void construct() {
		toretoiseType = EntityType.Builder.<ToretoiseEntity>create(ToretoiseEntity::new, EntityClassification.CREATURE)
				.size(2F, 1F)
				.setTrackingRange(80)
				.setUpdateInterval(3)
				.setShouldReceiveVelocityUpdates(true)
				.immuneToFire()
				.setCustomClientFactory((spawnEntity, world) -> new ToretoiseEntity(toretoiseType, world))
				.build("toretoise");
		
		RegistryHelper.register(toretoiseType, "toretoise");
		EntitySpawnHandler.registerSpawn(this, toretoiseType, EntityClassification.MONSTER, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, ToretoiseEntity::spawnPredicate, spawnConfig);
		EntitySpawnHandler.addEgg(toretoiseType, 0x55413b, 0x383237, spawnConfig);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		RenderingRegistry.registerEntityRenderingHandler(toretoiseType, ToretoiseRenderer::new);
	}
	
	@SubscribeEvent
	public void onLoot(LivingDropsEvent event) {
		if(disableIronFarms && event.getEntity().getType() == EntityType.IRON_GOLEM)
			event.getDrops().removeIf(e -> e.getItem().getItem() == Items.IRON_INGOT);
	}
	
}
