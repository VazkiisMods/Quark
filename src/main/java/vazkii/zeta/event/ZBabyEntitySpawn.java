package vazkii.zeta.event;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZBabyEntitySpawn extends IZetaPlayEvent {
    Mob getParentA();
    Mob getParentB();
    Player getCausedByPlayer();
    AgeableMob getChild();
    void setChild(AgeableMob proposedChild);

    interface Lowest extends ZBabyEntitySpawn { }
}
