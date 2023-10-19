package vazkii.zeta.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZLivingDeath extends IZetaPlayEvent {
	Entity getEntity();
	DamageSource getSource();

	interface Lowest extends ZLivingDeath { }
}
