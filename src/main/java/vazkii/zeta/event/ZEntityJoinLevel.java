package vazkii.zeta.event;

import net.minecraft.world.entity.Entity;
import vazkii.zeta.event.bus.Cancellable;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZEntityJoinLevel extends IZetaPlayEvent, Cancellable {
    Entity getEntity();
}
