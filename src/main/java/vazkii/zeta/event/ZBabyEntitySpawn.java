package vazkii.zeta.event;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZBabyEntitySpawn extends IZetaPlayEvent {
    Mob getParentA();
    Player getCausedByPlayer();

    interface Lowest extends ZBabyEntitySpawn { }
}
