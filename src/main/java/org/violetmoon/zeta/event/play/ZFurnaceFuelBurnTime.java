package org.violetmoon.zeta.event.play;

import net.minecraft.world.item.ItemStack;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZFurnaceFuelBurnTime extends IZetaPlayEvent {
	ItemStack getItemStack();
	void setBurnTime(int time);
}
