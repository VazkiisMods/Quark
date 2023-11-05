package vazkii.zetaimplforge.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import vazkii.zeta.event.ZPlayerInteract;

public class ForgeZPlayerInteract implements ZPlayerInteract {
    private final PlayerInteractEvent e;

    public ForgeZPlayerInteract(PlayerInteractEvent e) {
        this.e = e;
    }

    @Override
    public Player getEntity() {
        return e.getEntity();
    }

    @Override
    public InteractionHand getHand() {
        return e.getHand();
    }

    @Override
    public boolean isCanceled() {
        return e.isCanceled();
    }

    @Override
    public void setCanceled(boolean cancel) {
        e.setCanceled(cancel);
    }


    @Override
    public void setCancellationResult(InteractionResult result) {
        e.setCancellationResult(result);
    }

    public static class EntityInteractSpecific extends ForgeZPlayerInteract implements ZPlayerInteract.EntityInteractSpecific {
        private final PlayerInteractEvent.EntityInteractSpecific e;

        public EntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific e) {
            super(e);
            this.e = e;
        }

        @Override
        public Entity getTarget() {
            return e.getTarget();
        }
    }
}
