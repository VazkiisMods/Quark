package vazkii.zeta.event;

import net.minecraft.world.item.ItemStack;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZAnvilUpdate extends IZetaPlayEvent {
    ItemStack getLeft();
    ItemStack getRight();
    ItemStack getOutput();
    void setOutput(ItemStack output);
    void setCost(int cost);

    interface Lowest extends IZetaPlayEvent, ZAnvilUpdate { }
}
