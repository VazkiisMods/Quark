package org.violetmoon.zeta.event.play.entity;

import net.minecraft.world.entity.Entity;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZEntityConstruct extends IZetaPlayEvent {
	Entity getEntity();
}
