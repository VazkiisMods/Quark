package org.violetmoon.zeta.event.play;

import net.minecraft.world.item.ItemStack;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.helpers.PlayerGetter;

public interface ZAnvilRepair extends IZetaPlayEvent, PlayerGetter {
    ItemStack getOutput();
    ItemStack getLeft();
    ItemStack getRight();
    float getBreakChance();
    void setBreakChance(float breakChance);
}
