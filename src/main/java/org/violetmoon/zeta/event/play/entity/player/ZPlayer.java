package org.violetmoon.zeta.event.play.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.helpers.PlayerGetter;

public interface ZPlayer extends IZetaPlayEvent, PlayerGetter {
    interface BreakSpeed extends ZPlayer {
        BlockState getState();
        float getOriginalSpeed();
        void setNewSpeed(float newSpeed);
    }

    interface Clone extends ZPlayer {
        Player getOriginal();
    }

    interface LoggedIn extends ZPlayer { }

    interface LoggedOut extends ZPlayer { }
}
