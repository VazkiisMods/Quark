package vazkii.zetaimplforge.event;


import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import vazkii.zeta.event.ZLevelTick;

public class ForgeZLevelTick implements ZLevelTick {
    private final TickEvent.LevelTickEvent e;

    public ForgeZLevelTick(TickEvent.LevelTickEvent e) {
        this.e = e;
    }

    @Override
    public Level getLevel() {
        return e.level;
    }

    public static class Start extends ForgeZLevelTick implements ZLevelTick.Start {
        public Start(TickEvent.LevelTickEvent e) {
            super(e);
        }
    }

    public static class End extends ForgeZLevelTick implements ZLevelTick.End {
        public End(TickEvent.LevelTickEvent e) {
            super(e);
        }
    }
}
