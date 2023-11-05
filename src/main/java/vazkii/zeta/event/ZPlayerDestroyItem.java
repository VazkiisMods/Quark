package vazkii.zeta.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZPlayerDestroyItem extends IZetaPlayEvent {
    Player getEntity();
    ItemStack getOriginal();
    InteractionHand getHand();
}
