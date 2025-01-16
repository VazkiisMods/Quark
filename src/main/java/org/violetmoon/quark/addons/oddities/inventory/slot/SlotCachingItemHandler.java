package org.violetmoon.quark.addons.oddities.inventory.slot;

import net.neoforged.neoforge.items.IItemHandler;

@Deprecated(forRemoval = true)
public class SlotCachingItemHandler extends CachedItemHandlerSlot {
	public SlotCachingItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}
}
