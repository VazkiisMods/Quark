package vazkii.zetaimplforge.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import vazkii.zeta.event.ZPlayer;

public class ForgeZPlayer implements ZPlayer {
    private final PlayerEvent e;

    public ForgeZPlayer(PlayerEvent e) {
        this.e = e;
    }
    @Override
    public Player getEntity() {
        return e.getEntity();
    }

    public static class BreakSpeed extends ForgeZPlayer implements ZPlayer.BreakSpeed {
        private final PlayerEvent.BreakSpeed e;

        public BreakSpeed(PlayerEvent.BreakSpeed e) {
            super(e);
            this.e = e;
        }

        @Override
        public BlockState getState() {
            return e.getState();
        }

        @Override
        public float getOriginalSpeed() {
            return e.getOriginalSpeed();
        }

        @Override
        public void setNewSpeed(float newSpeed) {
            e.setNewSpeed(newSpeed);
        }
    }

    public static class Clone extends ForgeZPlayer implements ZPlayer.Clone {
        private final PlayerEvent.Clone e;

        public Clone(PlayerEvent.Clone e) {
            super(e);
            this.e = e;
        }

        @Override
        public Player getOriginal() {
            return e.getOriginal();
        }
    }

    public static class LoggedIn extends ForgeZPlayer implements ZPlayer.LoggedIn {
        public LoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
            super(e);
        }
    }

    public static class LoggedOut extends ForgeZPlayer implements ZPlayer.LoggedOut {
        public LoggedOut(PlayerEvent.PlayerLoggedOutEvent e) {
            super(e);
        }
    }
}
