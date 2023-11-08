package org.violetmoon.zeta.event.play.entity.living;

import net.minecraft.core.BlockPos;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.Resultable;
import org.violetmoon.zeta.event.bus.helpers.LivingGetter;

public interface ZSleepingLocationCheck extends IZetaPlayEvent, LivingGetter, Resultable {
    BlockPos getSleepingLocation();
}
