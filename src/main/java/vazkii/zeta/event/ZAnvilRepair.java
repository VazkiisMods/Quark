package vazkii.zeta.event;

import net.minecraft.world.item.ItemStack;
import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.helpers.PlayerGetter;

public interface ZAnvilRepair extends IZetaPlayEvent, PlayerGetter {
    ItemStack getOutput();
    ItemStack getLeft();
    ItemStack getRight();
    float getBreakChance();
    void setBreakChance(float breakChance);
}
