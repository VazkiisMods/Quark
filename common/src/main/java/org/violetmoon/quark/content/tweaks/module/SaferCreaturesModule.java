package org.violetmoon.quark.content.tweaks.module;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.play.entity.living.ZLivingFall;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

@ZetaLoadModule(category = "tweaks")
public class SaferCreaturesModule extends ZetaModule {

	@Config(description = "How many blocks should be subtracted from the rabbit fall height when calculating fall damage. 5 is the same value as vanilla frogs")
	public double heightReduction = 5.0;

	@Config
	public boolean enableSlimeFallDamageRemoval = true;

	@PlayEvent
	public void onFall(ZLivingFall event) {
		Entity e = event.getEntity();
		EntityType<?> type = e.getType();
		float dist = event.getDistance();

		if(type == EntityType.RABBIT)
			event.setDistance(Math.max(0, dist - (float) heightReduction));

		else if(type == EntityType.SLIME && enableSlimeFallDamageRemoval) {
			if(dist > 2) {
				Vec3 movement = e.getDeltaMovement();
				e.setDeltaMovement(movement.x, -2, movement.z);
			}

			event.setDistance(0);
		}
	}

}
