package vazkii.zetaimplforge.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
    public BlockPos getPos() {
        return e.getPos();
    }

    @Override
    public Level getLevel() {
        return e.getLevel();
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

    public static class EntityInteract extends ForgeZPlayerInteract implements ZPlayerInteract.EntityInteract {
        private final PlayerInteractEvent.EntityInteract e;

        public EntityInteract(PlayerInteractEvent.EntityInteract e) {
            super(e);
            this.e = e;
        }

        @Override
        public Entity getTarget() {
            return e.getTarget();
        }
    }

    public static class RightClickBlock extends ForgeZPlayerInteract implements ZPlayerInteract.RightClickBlock {
        private final PlayerInteractEvent.RightClickBlock e;

        public RightClickBlock(PlayerInteractEvent.RightClickBlock e) {
            super(e);
            this.e = e;
        }
    }

    public static class RightClickItem extends ForgeZPlayerInteract implements ZPlayerInteract.RightClickItem {
        private final PlayerInteractEvent.RightClickItem e;

        public RightClickItem(PlayerInteractEvent.RightClickItem e) {
            super(e);
            this.e = e;
        }

        @Override
        public ItemStack getItemStack() {
            return e.getItemStack();
        }
    }
}
