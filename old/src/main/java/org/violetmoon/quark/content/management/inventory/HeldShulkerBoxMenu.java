package org.violetmoon.quark.content.management.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import org.violetmoon.quark.api.ISortingLockedSlots;
import org.violetmoon.quark.content.management.module.ExpandedItemInteractionsModule;

public class HeldShulkerBoxMenu extends AbstractContainerMenu implements ISortingLockedSlots {

	private final Container container;
	private final Player player;
	public final int blockedSlot;

	public HeldShulkerBoxMenu(int p_40188_, Inventory p_40189_, int blockedSlot) {
		this(p_40188_, p_40189_, new SimpleContainer(27), blockedSlot);
	}

	public HeldShulkerBoxMenu(int p_40191_, Inventory p_40192_, Container p_40193_, int blockedSlot) {
		super(ExpandedItemInteractionsModule.heldShulkerBoxMenuType, p_40191_);
		checkContainerSize(p_40193_, 27);
		this.container = p_40193_;
		this.player = p_40192_.player;
		this.blockedSlot = blockedSlot;
		p_40193_.startOpen(p_40192_.player);

		for(int k = 0; k < 3; ++k) {
			for(int l = 0; l < 9; ++l) {
				this.addSlot(new ShulkerBoxSlot(p_40193_, l + k * 9, 8 + l * 18, 18 + k * 18));
			}
		}

		for(int i1 = 0; i1 < 3; ++i1) {
			for(int k1 = 0; k1 < 9; ++k1) {
				int id = k1 + i1 * 9 + 9;
				if(id != blockedSlot)
					this.addSlot(new Slot(p_40192_, id, 8 + k1 * 18, 84 + i1 * 18));
			}
		}

		for(int j1 = 0; j1 < 9; ++j1) {
			if(j1 != blockedSlot)
				this.addSlot(new Slot(p_40192_, j1, 8 + j1 * 18, 142));
		}
	}

	public static HeldShulkerBoxMenu fromNetwork(int windowId, Inventory playerInventory, FriendlyByteBuf buf) {
		int slot = buf.readInt();
		HeldShulkerBoxContainer container = new HeldShulkerBoxContainer(playerInventory.player, slot);
		return new HeldShulkerBoxMenu(windowId, playerInventory, container, slot);
	}

	@Override
	public boolean stillValid(Player p_40195_) {
		return this.container.stillValid(p_40195_);
	}

	@Override
	public ItemStack quickMoveStack(Player p_40199_, int p_40200_) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(p_40200_);
		if(slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if(p_40200_ < this.container.getContainerSize()) {
				if(!this.moveItemStackTo(itemstack1, this.container.getContainerSize(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if(!this.moveItemStackTo(itemstack1, 0, this.container.getContainerSize(), false)) {
				return ItemStack.EMPTY;
			}

			if(itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return itemstack;
	}

	@Override
	public void suppressRemoteUpdates() {
		super.suppressRemoteUpdates();
		player.inventoryMenu.suppressRemoteUpdates();
	}

	@Override
	public void resumeRemoteUpdates() {
		super.resumeRemoteUpdates();
		player.inventoryMenu.resumeRemoteUpdates();
	}

	@Override
	public void removed(Player p_40197_) {
		super.removed(p_40197_);
		this.container.stopOpen(p_40197_);
	}

	@Override
	public int[] getSortingLockedSlots(boolean sortingPlayerInventory) {
		return sortingPlayerInventory ? new int[] { blockedSlot } : null;
	}

}
