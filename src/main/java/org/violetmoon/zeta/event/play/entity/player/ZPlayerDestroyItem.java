package org.violetmoon.zeta.event.play.entity.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZPlayerDestroyItem extends IZetaPlayEvent {
    Player getEntity();
    ItemStack getOriginal();
    InteractionHand getHand();
}
