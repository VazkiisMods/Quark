package vazkii.zeta.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import vazkii.zeta.event.bus.Cancellable;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZPlayerInteract extends IZetaPlayEvent, Cancellable {
    Player getEntity();
    InteractionHand getHand();
    void setCancellationResult(InteractionResult result);
    interface EntityInteractSpecific extends ZPlayerInteract {
        Entity getTarget();
    }
}
