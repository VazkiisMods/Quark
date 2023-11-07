package vazkii.zeta.event;

import net.minecraft.core.BlockPos;
import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.Living;
import vazkii.zeta.event.bus.Resultable;

public interface ZSleepingLocationCheck extends IZetaPlayEvent, Living, Resultable {
    BlockPos getSleepingLocation();
}
