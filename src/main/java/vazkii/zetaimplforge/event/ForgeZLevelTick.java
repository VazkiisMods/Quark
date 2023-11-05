package vazkii.zetaimplforge.event;

import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import vazkii.zeta.event.ZLevelTick;

public class ForgeZLevelTick implements ZLevelTick {
    private final LevelTickEvent e;

    public ForgeZLevelTick(LevelTickEvent e) {
        this.e = e;
    }

    @Override
    public Level getLevel() {
        return e.level;
    }

    @Override
    public Phase getPhase() {
        return e.phase;
    }
}
