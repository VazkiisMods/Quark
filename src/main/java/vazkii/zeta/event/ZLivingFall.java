package vazkii.zeta.event;

import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.helpers.LivingGetter;

public interface ZLivingFall extends IZetaPlayEvent, LivingGetter {
    float getDistance();
    void setDistance(float distance);
}
