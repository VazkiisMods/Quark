package org.violetmoon.zeta.event.play.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZLivingDeath extends IZetaPlayEvent {
	Entity getEntity();
	DamageSource getSource();

	interface Lowest extends ZLivingDeath { }
}
