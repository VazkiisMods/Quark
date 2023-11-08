package org.violetmoon.zetaimplforge.event.play.entity.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import org.violetmoon.zeta.event.play.entity.player.ZPlayerDestroyItem;

public class ForgeZPlayerDestroyItem implements ZPlayerDestroyItem {
    private final PlayerDestroyItemEvent e;

    public ForgeZPlayerDestroyItem(PlayerDestroyItemEvent e) {
        this.e = e;
    }

    @Override
    public Player getEntity() {
        return e.getEntity();
    }

    @Override
    public ItemStack getOriginal() {
        return e.getOriginal();
    }

    @Override
    public InteractionHand getHand() {
        return e.getHand();
    }
}
