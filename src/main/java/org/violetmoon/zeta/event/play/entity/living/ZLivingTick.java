package org.violetmoon.zeta.event.play.entity.living;

import net.minecraft.world.entity.LivingEntity;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZLivingTick extends IZetaPlayEvent {
	LivingEntity getEntity();
}
