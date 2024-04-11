package org.violetmoon.quark.content.tweaks.module;

import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.phys.Vec3;

import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.play.entity.living.ZLivingTick;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

@ZetaLoadModule(category = "tweaks")
public class HorsesSwimModule extends ZetaModule {

	@PlayEvent
	public void tick(ZLivingTick event) {
		if(event.getEntity() instanceof AbstractHorse honse) {
			boolean ridden = !honse.getPassengers().isEmpty();
			boolean water = honse.isInWater();
			if(ridden && water) {
				boolean tallWater = honse.level().isWaterAt(honse.blockPosition().below());

				if(tallWater)
					honse.move(MoverType.PLAYER, new Vec3(0, 0.1, 0));
			}
		}
	}

}
