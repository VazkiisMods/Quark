package vazkii.quark.oddities.container;

import javax.annotation.Nonnull;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotCachingItemHandler extends SlotItemHandler {
	public SlotCachingItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Nonnull
	@Override
	public ItemStack getStack() {
		if (caching)
			return cached;
		return super.getStack();
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int amount) {
		if (caching) {
			ItemStack newStack = cached.copy();
			int trueAmount = Math.min(amount, cached.getCount());
			cached.shrink(trueAmount);
			newStack.setCount(trueAmount);
			return newStack;
		}
		return super.decrStackSize(amount);
	}

	@Override
	public void putStack(@Nonnull ItemStack stack) {
		super.putStack(stack);
		if (caching)
			cached = stack;
	}

	private ItemStack cached = ItemStack.EMPTY;

	private boolean caching = false;

	public static void cache(Container container) {
		for (Slot slot : container.inventorySlots) {
			if (slot instanceof SlotCachingItemHandler) {
				SlotCachingItemHandler thisSlot = (SlotCachingItemHandler) slot;
				thisSlot.cached = slot.getStack();
				thisSlot.caching = true;
			}
		}
	}

	public static void applyCache(Container container) {
		for (Slot slot : container.inventorySlots) {
			if (slot instanceof SlotCachingItemHandler) {
				SlotCachingItemHandler thisSlot = (SlotCachingItemHandler) slot;
				if (thisSlot.caching) {
					slot.putStack(thisSlot.cached);
					thisSlot.caching = false;
				}
			}
		}
	}
}