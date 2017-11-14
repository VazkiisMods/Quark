package vazkii.quark.tweaks.feature;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class SquidsInkYou extends Feature {

	int time = 80;
	
	@Override
	public void setupConfig() {
		time = loadPropInt("Blindness Time", "How long should blindness last upon hitting a squid, in ticks", time);
	}
	
	@SubscribeEvent
	public void onHurt(LivingHurtEvent event) {
		Entity e = event.getEntity();
		if(e instanceof EntitySquid && !e.world.isRemote) {
			List<EntityPlayer> players = e.world.getEntitiesWithinAABB(EntityPlayer.class, e.getEntityBoundingBox().grow(4, 4, 4));
			for(EntityPlayer player : players)
				player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, time, 0));
			
			WorldServer ws = (WorldServer) e.world;
			ws.spawnParticle(EnumParticleTypes.SMOKE_LARGE, e.posX + e.width / 2, e.posY + e.height / 2, e.posZ + e.width / 2, 100, 0, 0, 0, 0.02);
		}
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
