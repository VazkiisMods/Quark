package org.violetmoon.zeta.event.play.entity;

import net.minecraft.world.entity.Entity;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.Resultable;

public interface ZEntityMobGriefing extends IZetaPlayEvent, Resultable {
    Entity getEntity();
}
