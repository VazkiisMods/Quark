package vazkii.zeta.event;

import net.minecraft.world.entity.Entity;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZEntityTeleport extends IZetaPlayEvent {
    Entity getEntity();
    double getTargetX();
    double getTargetY();
    double getTargetZ();
    void setTargetX(double targetX);
    void setTargetY(double targetY);
    void setTargetZ(double targetZ);
}
