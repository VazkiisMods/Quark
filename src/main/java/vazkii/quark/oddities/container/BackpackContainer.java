package vazkii.quark.oddities.container;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import vazkii.arl.util.InventoryIIH;
import vazkii.quark.oddities.module.BackpackModule;

public class BackpackContainer extends PlayerContainer {

	public BackpackContainer(int windowId, PlayerEntity player) {
		super(player.inventory, !player.world.isRemote, player);

		this.windowId = windowId;
		
		for(Slot slot : inventorySlots)
			if (slot.inventory == player.inventory && slot.getSlotIndex() < player.inventory.getSizeInventory() - 5)
				slot.yPos += 58;

		Slot anchor = inventorySlots.get(9);
		int left = anchor.xPos;
		int top = anchor.yPos - 58;

		ItemStack backpack = player.inventory.armorInventory.get(2);
		if(backpack.getItem() == BackpackModule.backpack) {
			InventoryIIH inv = new InventoryIIH(backpack);

			for(int i = 0; i < 3; ++i)
				for(int j = 0; j < 9; ++j) {
					int k = j + i * 9;
					addSlot(new SlotCachingItemHandler(inv, k, left + j * 18, top + i * 18));
				}
		}
	}

	public static BackpackContainer fromNetwork(int windowId, PlayerInventory playerInventory, PacketBuffer buf) {
		return new BackpackContainer(windowId, playerInventory.player);
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(@Nonnull PlayerEntity playerIn, int index) {
		ItemStack baseStack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			baseStack = stack.copy();
			EquipmentSlotType slotType = stack.getEquipmentSlot();
			int equipIndex = 8 - (slotType == null ? 0 : slotType.getIndex());

			if (index == 0) {
				if (!this.mergeItemStack(stack, 9, 45, false) && !this.mergeItemStack(stack, 46, 73, false)) 
					return ItemStack.EMPTY;

				slot.onSlotChange(stack, baseStack);
			} else if (index < 5) {
				if (!this.mergeItemStack(stack, 9, 45, false)) 
					return ItemStack.EMPTY;
			} else if (index < 9) {
				if (!this.mergeItemStack(stack, 9, 45, false) && !this.mergeItemStack(stack, 46, 73, false)) 
					return ItemStack.EMPTY;
			} else if (slotType != null && slotType.getSlotType() == Group.ARMOR && !this.inventorySlots.get(equipIndex).getHasStack()) {
				if (!this.mergeItemStack(stack, equipIndex, equipIndex + 1, false)) 
					return ItemStack.EMPTY;
			} else if (slotType != null && slotType == EquipmentSlotType.OFFHAND && !this.inventorySlots.get(45).getHasStack()) {
				if (!this.mergeItemStack(stack, 45, 46, false)) 
					return ItemStack.EMPTY;
			} else if (index < 36) {
				if (!this.mergeItemStack(stack, 46, 73, false) && !this.mergeItemStack(stack, 36, 45, false)) 
					return ItemStack.EMPTY;
			} else if (index < 73) {
				if (!this.mergeItemStack(stack, 9, 36, false)) 
					return ItemStack.EMPTY;
			} else {
				if (!this.mergeItemStack(stack, 46, 73, false) && !this.mergeItemStack(stack, 9, 45, false)) 
					return ItemStack.EMPTY;
			}

			if (stack.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else slot.onSlotChanged();

			if (stack.getCount() == baseStack.getCount())
				return ItemStack.EMPTY;

			ItemStack remainder = slot.onTake(playerIn, stack);

			if (index == 0) 
				playerIn.dropItem(remainder, false);
		}

		return baseStack;
	}

	// Shamelessly stolen from CoFHCore because KL is awesome
	// and was like yeah just take whatever you want lol
	// https://github.com/CoFH/CoFHCore/blob/d4a79b078d257e88414f5eed598d57490ec8e97f/src/main/java/cofh/core/util/helpers/InventoryHelper.java
	@Override
	public boolean mergeItemStack(ItemStack stack, int start, int length, boolean r) {
		boolean successful = false;
		int i = !r ? start : length - 1;
		int iterOrder = !r ? 1 : -1;

		Slot slot;
		ItemStack existingStack;

		if(stack.isStackable()) while (stack.getCount() > 0 && (!r && i < length || r && i >= start)) {
			slot = inventorySlots.get(i);

			existingStack = slot.getStack();

			if (!existingStack.isEmpty()) {
				int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
				int rmv = Math.min(maxStack, stack.getCount());

				if (slot.isItemValid(cloneStack(stack, rmv)) && existingStack.getItem().equals(stack.getItem()) && ItemStack.areItemStackTagsEqual(stack, existingStack)) {
					int existingSize = existingStack.getCount() + stack.getCount();

					if (existingSize <= maxStack) {
						stack.setCount(0);
						existingStack.setCount(existingSize);
						slot.putStack(existingStack);
						successful = true;
					} else if (existingStack.getCount() < maxStack) {
						stack.shrink(maxStack - existingStack.getCount());
						existingStack.setCount(maxStack);
						slot.putStack(existingStack);
						successful = true;
					}
				}
			}
			i += iterOrder;
		}
		if(stack.getCount() > 0) {
			i = !r ? start : length - 1;
			while(stack.getCount() > 0 && (!r && i < length || r && i >= start)) {
				slot = inventorySlots.get(i);
				existingStack = slot.getStack();

				if(existingStack.isEmpty()) {
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					int rmv = Math.min(maxStack, stack.getCount());

					if(slot.isItemValid(cloneStack(stack, rmv))) {
						existingStack = stack.split(rmv);
						slot.putStack(existingStack);
						successful = true;
					}
				}
				i += iterOrder;
			}
		}
		return successful;
	}

	@Nonnull
	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		SlotCachingItemHandler.cache(this);
		ItemStack stack = super.slotClick(slotId, dragType, clickTypeIn, player);
		SlotCachingItemHandler.applyCache(this);
		return stack;
	}

	private static ItemStack cloneStack(ItemStack stack, int size) {
		if(stack.isEmpty())
			return ItemStack.EMPTY;

		ItemStack copy = stack.copy();
		copy.setCount(size);
		return copy;
	}

	public static void saveCraftingInventory(PlayerEntity player) {
		CraftingInventory crafting = ((PlayerContainer) player.openContainer).craftMatrix;
		for(int i = 0; i < crafting.getSizeInventory(); i++) {
			ItemStack stack = crafting.getStackInSlot(i);
			if(!stack.isEmpty() && !player.addItemStackToInventory(stack))
				player.dropItem(stack, false);
		}
	}

	@Override
	public ContainerType<?> getType() {
		return BackpackModule.container;
	}

}
