package vazkii.zetaimplforge.event;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import vazkii.zeta.event.ZAnvilUpdate;
import vazkii.zeta.event.ZBabyEntitySpawn;

public class ForgeZBabyEntitySpawn implements ZBabyEntitySpawn {
    private final BabyEntitySpawnEvent e;

    public ForgeZBabyEntitySpawn(BabyEntitySpawnEvent e) {
        this.e = e;
    }

    @Override
    public Mob getParentA() {
        return e.getParentA();
    }

    @Override
    public Player getCausedByPlayer() {
        return e.getCausedByPlayer();
    }

    public static class Lowest extends ForgeZBabyEntitySpawn implements ZBabyEntitySpawn.Lowest {
        public Lowest(BabyEntitySpawnEvent e) {
            super(e);
        }
    }
}
