package vazkii.quark.content.tweaks.module;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "tweaks", hasSubscriptions = true)
public class PoisonPotatoUsageModule extends QuarkModule {

	private static final String TAG_POISONED = "quark:poison_potato_applied";

	@Config public static double chance = 0.1;
	@Config public static boolean poisonEffect = true;
	
	@Hint Item poison_potato = Items.POISONOUS_POTATO;
	
	public static QuarkGenericTrigger poisonBabyTrigger;
	
	@LoadEvent
	public final void register(ZRegister event) {
		poisonBabyTrigger = QuarkAdvancementHandler.registerGenericTrigger("poison_baby");
	}

	@SubscribeEvent
	public void onInteract(EntityInteract event) {
		if(event.getItemStack().getItem() == Items.POISONOUS_POTATO && canPoison(event.getTarget())) {
			LivingEntity entity = (LivingEntity) event.getTarget();
			
			if(!event.getLevel().isClientSide) {
				Vec3 pos = entity.position();
				if(event.getEntity().isCreative() || entity.level.random.nextDouble() < chance) {
					entity.playSound(SoundEvents.GENERIC_EAT, 0.5f, 0.25f);
					entity.level.addParticle(ParticleTypes.ENTITY_EFFECT, pos.x, pos.y, pos.z, 0.2, 0.8, 0);
					poisonEntity(entity);
					
					if(event.getEntity() instanceof ServerPlayer sp)
						poisonBabyTrigger.trigger(sp);
					
					if (poisonEffect)
						entity.addEffect(new MobEffectInstance(MobEffects.POISON, 80));
				} else {
					entity.playSound(SoundEvents.GENERIC_EAT, 0.5f, 0.5f + entity.level.random.nextFloat() / 2);
					entity.level.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 0, 0.1, 0);
				}

				if (!event.getEntity().getAbilities().instabuild)
					event.getItemStack().shrink(1);

			} else event.getEntity().swing(event.getHand());

		}
	}

	private boolean canPoison(Entity entity) {
		return !isEntityPoisoned(entity) &&
				(entity instanceof AgeableMob ageable && ageable.isBaby()
				|| entity instanceof Tadpole);
	}

	@SubscribeEvent
	public void onEntityUpdate(LivingTickEvent event) {
		if(event.getEntity() instanceof Animal animal) {
			if(animal.isBaby() && isEntityPoisoned(animal))
				animal.setAge(-24000);
		}
		
		else if(event.getEntity() instanceof Tadpole tadpole) {
			if(isEntityPoisoned(tadpole))
				tadpole.setAge(0);
		}
	}

	private boolean isEntityPoisoned(Entity e) {
		return e.getPersistentData().getBoolean(TAG_POISONED);
	}

	private void poisonEntity(Entity e) {
		e.getPersistentData().putBoolean(TAG_POISONED, true);
	}

}
