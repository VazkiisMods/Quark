package vazkii.zeta.event;

import net.minecraft.world.level.Level;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZLevelTick extends IZetaPlayEvent {
    Level getLevel();

    interface Start extends ZLevelTick { }
    interface End extends ZLevelTick { }
}
