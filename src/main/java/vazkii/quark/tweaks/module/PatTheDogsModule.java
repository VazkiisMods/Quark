package vazkii.quark.tweaks.module;

import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.Config;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.tweaks.ai.NuzzleGoal;
import vazkii.quark.tweaks.ai.WantLoveGoal;
import vazkii.quark.world.entity.FoxhoundEntity;

/**
 * @author WireSegal
 * Created at 11:25 AM on 9/2/19.
 */
@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class PatTheDogsModule extends Module {
    @Config(description = "How many ticks it takes for a dog to want affection after being pet/tamed; leave -1 to disable")
    public static int dogsWantLove = -1;

    @SubscribeEvent
    public void onWolfAppear(EntityJoinWorldEvent event) {
        if (dogsWantLove > 0 && event.getEntity() instanceof WolfEntity) {
            WolfEntity wolf = (WolfEntity) event.getEntity();
            boolean alreadySetUp = wolf.goalSelector.goals.stream().anyMatch((goal) -> goal.getGoal() instanceof WantLoveGoal);

            if (!alreadySetUp) {
                wolf.goalSelector.addGoal(4, new NuzzleGoal(wolf, 0.5F, 16, 2, SoundEvents.ENTITY_WOLF_WHINE));
                wolf.goalSelector.addGoal(5, new WantLoveGoal(wolf, 0.2F));
            }
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event) {
        if(event.getTarget() instanceof WolfEntity) {
            WolfEntity wolf = (WolfEntity) event.getTarget();
            PlayerEntity player = event.getPlayer();

            if(player.isDiscrete() && player.getHeldItemMainhand().isEmpty()) {
                if(event.getHand() == Hand.MAIN_HAND && WantLoveGoal.canPet(wolf)) {
                    if(player.world instanceof ServerWorld) {
                    	Vec3d pos = wolf.getPositionVec();
                        ((ServerWorld) player.world).spawnParticle(ParticleTypes.HEART, pos.x, pos.y + 0.5, pos.z, 1, 0, 0, 0, 0.1);
                        wolf.playSound(SoundEvents.ENTITY_WOLF_WHINE, 1F, 0.5F + (float) Math.random() * 0.5F);
                    } else player.swingArm(Hand.MAIN_HAND);

                    WantLoveGoal.setPetTime(wolf);

                    if (wolf instanceof FoxhoundEntity && !player.isInWater() && !player.isImmuneToFire() && !player.isCreative())
                        player.setFire(5);
                }

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onTame(AnimalTameEvent event) {
        if(event.getAnimal() instanceof WolfEntity) {
            WolfEntity wolf = (WolfEntity) event.getAnimal();
            WantLoveGoal.setPetTime(wolf);
        }
    }

}
