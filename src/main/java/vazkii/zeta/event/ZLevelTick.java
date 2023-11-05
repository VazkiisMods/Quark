package vazkii.zeta.event;

import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.Phase;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZLevelTick extends IZetaPlayEvent {
    Level getLevel();
    Phase getPhase();
}
