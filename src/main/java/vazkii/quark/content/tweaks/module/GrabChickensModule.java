package vazkii.quark.content.tweaks.module;

import java.util.List;

import net.minecraft.client.model.ChickenModel;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "tweaks", hasSubscriptions = true)
public class GrabChickensModule extends QuarkModule {
	
	@Config
	private static boolean needsNoHelmet = true;
	
	@Config(description = "Set to 0 to disable")
	private static int slownessLevel = 1;

	private static boolean staticEnabled;
	
	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		staticEnabled = enabled;
	}
	
	@SubscribeEvent
	public void playerInteract(PlayerInteractEvent.EntityInteract event) {
		Entity target = event.getTarget();
		Player player = event.getEntity();
		Level level = event.getLevel();
		
		if(staticEnabled && event.getHand() == InteractionHand.MAIN_HAND 
				&& !player.isCrouching() 
				&& !(player instanceof FakePlayer)
				&& player.getMainHandItem().isEmpty()
				&& canPlayerHostChicken(player) 
				&& target.getType() == EntityType.CHICKEN
				&& !((Chicken) target).isBaby()) {
			List<Entity> passengers = player.getPassengers();
			
			boolean changed = false;
			
			if(passengers.contains(target)) {
				if(!level.isClientSide)
					target.stopRiding();
				
				changed = true;
			} else if(passengers.isEmpty()) {
				if(!level.isClientSide)
					target.startRiding(player, false);
				
				changed = true;
			}
			
			if(changed) {
				if(level instanceof ServerLevel slevel)
					slevel.getChunkSource().chunkMap.broadcast(target, new ClientboundSetPassengersPacket(player));
				else player.swing(InteractionHand.MAIN_HAND);
			}
		}
	}
	
	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		Player player = event.player;
		Level level = player.level;
		
		if(player.hasPassenger(e -> e.getType() == EntityType.CHICKEN)) {
			if(!canPlayerHostChicken(player)) {
				player.ejectPassengers();
				
				if(level instanceof ServerLevel slevel)
					slevel.getChunkSource().chunkMap.broadcast(player, new ClientboundSetPassengersPacket(player));
			} else if(!player.hasEffect(MobEffects.SLOW_FALLING)) {
				player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 5, 0, true, false));
				
				if(slownessLevel > 0 && !player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))
					player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, slownessLevel - 1, true, false));
			}
		}
	}
	
	private boolean canPlayerHostChicken(Player player) {
		return (!needsNoHelmet || player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) && !player.isUnderWater();
	}

	@OnlyIn(Dist.CLIENT)
	public static void setRenderChickenFeetStatus(Chicken entity, ChickenModel<Chicken> model) {
		if(!staticEnabled)
			return;
		
		boolean should = entity.getVehicle() == null || entity.getVehicle().getType() != EntityType.PLAYER;
		model.leftLeg.visible = should;
		model.rightLeg.visible = should;
	}
	
}
