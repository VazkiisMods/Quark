package vazkii.quark.content.mobs.module;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.EntityAttributeHandler;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.handler.advancement.mod.MonsterHunterModifier;
import vazkii.quark.base.handler.advancement.mod.TwoByTwoModifier;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.base.module.config.type.CostSensitiveEntitySpawnConfig;
import vazkii.quark.base.module.config.type.EntitySpawnConfig;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.mobs.client.render.entity.FoxhoundRenderer;
import vazkii.quark.content.mobs.entity.Foxhound;
import vazkii.zeta.client.event.ZClientSetup;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZLivingChangeTarget;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.ZSleepingLocationCheck;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.event.bus.ZResult;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

import static net.minecraftforge.event.entity.living.LivingChangeTargetEvent.LivingTargetType.BEHAVIOR_TARGET;

/**
 * @author WireSegal
 * Created at 5:00 PM on 9/26/19.
 */
@ZetaLoadModule(category = "mobs")
public class FoxhoundModule extends ZetaModule {

	public static EntityType<Foxhound> foxhoundType;

	@Config(description = "The chance coal will tame a foxhound")
	public static double tameChance = 0.05;

	@Config(flag = "foxhound_furnace")
	public static boolean foxhoundsSpeedUpFurnaces = true;
	
	@Config
	public static EntitySpawnConfig spawnConfig = new EntitySpawnConfig(30, 1, 2, CompoundBiomeConfig.fromBiomeReslocs(false, "minecraft:nether_wastes", "minecraft:basalt_deltas"));

	@Config
	public static EntitySpawnConfig lesserSpawnConfig = new CostSensitiveEntitySpawnConfig(2, 1, 1, 0.7, 0.15, CompoundBiomeConfig.fromBiomeReslocs(false, "minecraft:soul_sand_valley"));

	public static TagKey<Block> foxhoundSpawnableTag;
	
	public static QuarkGenericTrigger foxhoundFurnaceTrigger;

	@LoadEvent
	public final void register(ZRegister event) {
		foxhoundType = EntityType.Builder.of(Foxhound::new, MobCategory.CREATURE)
				.sized(0.8F, 0.8F)
				.clientTrackingRange(8)
				.fireImmune()
				.setCustomClientFactory((spawnEntity, world) -> new Foxhound(foxhoundType, world))
				.build("foxhound");
		Quark.ZETA.registry.register(foxhoundType, "foxhound", Registry.ENTITY_TYPE_REGISTRY);

		EntitySpawnHandler.registerSpawn(foxhoundType, MobCategory.MONSTER, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Foxhound::spawnPredicate, spawnConfig);
		EntitySpawnHandler.track(foxhoundType, MobCategory.MONSTER, lesserSpawnConfig, true);

		EntitySpawnHandler.addEgg(this, foxhoundType, 0x890d0d, 0xf2af4b, spawnConfig);

		EntityAttributeHandler.put(foxhoundType, Wolf::createAttributes);
		
		QuarkAdvancementHandler.addModifier(new MonsterHunterModifier(this, ImmutableSet.of(foxhoundType)));
		QuarkAdvancementHandler.addModifier(new TwoByTwoModifier(this, ImmutableSet.of(foxhoundType)));
		
		foxhoundFurnaceTrigger = QuarkAdvancementHandler.registerGenericTrigger("foxhound_furnace");
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		foxhoundSpawnableTag = BlockTags.create(new ResourceLocation(Quark.MOD_ID, "foxhound_spawnable"));
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		EntityRenderers.register(foxhoundType, FoxhoundRenderer::new);
	}

	@PlayEvent
	public void onAggro(ZLivingChangeTarget event) {
		if (event.getNewTarget() != null
			&& event.getTargetType() != BEHAVIOR_TARGET
			&& event.getEntity().getType() == EntityType.IRON_GOLEM
			&& event.getNewTarget().getType() == foxhoundType
			&& ((Foxhound) event.getNewTarget()).isTame())
			event.setCanceled(true);
	}

	@PlayEvent
	public void onSleepCheck(ZSleepingLocationCheck event) {
		if(event.getEntity() instanceof Foxhound) {
			BlockPos pos = event.getSleepingLocation();
			Level world = event.getEntity().level;

			BlockPos below = pos.below();
			BlockState belowState = world.getBlockState(below);
			int light = zeta.blockExtensions.get(belowState).getLightEmissionZeta(belowState, world, below);
			if(light > 2)
				event.setResult(ZResult.ALLOW);
		}
	}
}
