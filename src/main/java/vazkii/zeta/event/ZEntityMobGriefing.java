package vazkii.zeta.event;

import net.minecraft.world.entity.Entity;
import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.Resultable;

public interface ZEntityMobGriefing extends IZetaPlayEvent, Resultable {
    Entity getEntity();
}
