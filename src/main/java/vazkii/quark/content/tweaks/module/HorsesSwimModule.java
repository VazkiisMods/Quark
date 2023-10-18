package vazkii.quark.content.tweaks.module;

import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = "tweaks", hasSubscriptions = true)
public class HorsesSwimModule extends QuarkModule {

	@SubscribeEvent
	public void tick(LivingTickEvent event) {
		if(event.getEntity() instanceof AbstractHorse honse) {
			boolean ridden = !honse.getPassengers().isEmpty();
			boolean water = honse.isInWater();
			if(ridden && water) {
				boolean tallWater = honse.level.isWaterAt(honse.blockPosition().below());
				
				if(tallWater)
					honse.move(MoverType.PLAYER, new Vec3(0, 0.1, 0));
			}
		}
	}
	
}
