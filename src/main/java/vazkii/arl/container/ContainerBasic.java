package vazkii.arl.container;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class ContainerBasic<T extends Container> extends AbstractContainerMenu {

	protected final T tile;
	protected final int tileSlots;

	public ContainerBasic(MenuType<?> type, int windowId, Inventory playerInv, T tile) {
		super(type, windowId);
		this.tile = tile;
		tileSlots = addSlots();

		for(int i = 0; i < 3; ++i)
			for(int j = 0; j < 9; ++j)
				addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for(int k = 0; k < 9; ++k)
			addSlot(new Slot(playerInv, k, 8 + k * 18, 142));
	}

	public abstract int addSlots(); 

	@Override
	public boolean stillValid(@Nonnull Player playerIn) {
		return tile.stillValid(playerIn);
	}

	@Nonnull
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if(slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();

			if(index < tileSlots) {
				if(!moveItemStackTo(itemstack1, tileSlots, slots.size(), true))
					return ItemStack.EMPTY;
			}
			else if(!moveItemStackTo(itemstack1, 0, tileSlots, false))
				return ItemStack.EMPTY;

			if(itemstack1.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
		}

		return itemstack;
	}

	// Shamelessly stolen from CoFHCore because KL is awesome
	// and was like yeah just take whatever you want lol
	// https://github.com/CoFH/CoFHCore/blob/d4a79b078d257e88414f5eed598d57490ec8e97f/src/main/java/cofh/core/util/helpers/InventoryHelper.java
	@Override
	public boolean moveItemStackTo(ItemStack stack, int start, int length, boolean r) {
		boolean successful = false;
		int i = !r ? start : length - 1;
		int iterOrder = !r ? 1 : -1;

		Slot slot;
		ItemStack existingStack;

		if(stack.isStackable()) {
			while(stack.getCount() > 0 && (!r && i < length || r && i >= start)) {
				slot = slots.get(i);

				existingStack = slot.getItem();

				if(!existingStack.isEmpty()) {
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize());
					int rmv = Math.min(maxStack, stack.getCount());

					if(slot.mayPlace(cloneStack(stack, rmv)) && existingStack.getItem().equals(stack.getItem()) && ItemStack.tagMatches(stack, existingStack)) {
						int existingSize = existingStack.getCount() + stack.getCount();

						if(existingSize <= maxStack) {
							stack.setCount(0);
							existingStack.setCount(existingSize);
							slot.set(existingStack);
							successful = true;
						} else if(existingStack.getCount() < maxStack) {
							stack.shrink(maxStack - existingStack.getCount());
							existingStack.setCount(maxStack);
							slot.set(existingStack);
							successful = true;
						}
					}
				}
				i += iterOrder;
			}
		}
		if(stack.getCount() > 0) {
			i = !r ? start : length - 1;
			while(stack.getCount() > 0 && (!r && i < length || r && i >= start)) {
				slot = slots.get(i);
				existingStack = slot.getItem();

				if(existingStack.isEmpty()) {
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize());
					int rmv = Math.min(maxStack, stack.getCount());

					if(slot.mayPlace(cloneStack(stack, rmv))) {
						existingStack = stack.split(rmv);
						slot.set(existingStack);
						successful = true;
					}
				}
				i += iterOrder;
			}
		}
		return successful;
	}

	private static ItemStack cloneStack(ItemStack stack, int size) {
		if(stack.isEmpty())
			return ItemStack.EMPTY;

		ItemStack copy = stack.copy();
		copy.setCount(size);
		return copy;
	}
}
