package org.violetmoon.zeta.event.play;

import net.minecraft.world.level.Level;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZLevelTick extends IZetaPlayEvent {
    Level getLevel();

    interface Start extends ZLevelTick { }
    interface End extends ZLevelTick { }
}
