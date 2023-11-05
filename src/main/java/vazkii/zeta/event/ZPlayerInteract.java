package vazkii.zeta.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZPlayerInteract extends IZetaPlayEvent {
    Player getEntity();
    InteractionHand getHand();
    void setCanceled(boolean cancel);
    void setCancellationResult(InteractionResult result);
    interface EntityInteractSpecific extends IZetaPlayEvent, ZPlayerInteract {
        Entity getTarget();
    }
}
