package org.violetmoon.zeta.event.play;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZAnvilUpdate extends IZetaPlayEvent {
    ItemStack getLeft();
    ItemStack getRight();
    String getName();
    ItemStack getOutput();
    void setOutput(ItemStack output);
    void setCost(int cost);
    int getMaterialCost();
    void setMaterialCost(int materialCost);
    Player getPlayer();

    interface Lowest extends ZAnvilUpdate { }
    interface Highest extends ZAnvilUpdate { }
}
