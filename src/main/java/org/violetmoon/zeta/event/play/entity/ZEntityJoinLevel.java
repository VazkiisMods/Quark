package org.violetmoon.zeta.event.play.entity;

import net.minecraft.world.entity.Entity;
import org.violetmoon.zeta.event.bus.Cancellable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZEntityJoinLevel extends IZetaPlayEvent, Cancellable {
    Entity getEntity();
}
