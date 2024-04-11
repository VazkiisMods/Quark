package org.violetmoon.quark.addons.oddities.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;

import org.jetbrains.annotations.NotNull;

import org.violetmoon.quark.addons.oddities.module.CrateModule;
import org.violetmoon.quark.base.handler.SortingHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WireSegal
 *         Created at 8:53 AM on 4/8/22.
 */
public class CrateItemHandler extends ItemStackHandler {

	private boolean needsUpdate = false;

	private final int maxItems;

	public int displayTotal = 0;
	public int displaySlots = 0;

	private int cachedTotal = -1;

	public CrateItemHandler() {
		super(CrateModule.maxItems);
		maxItems = CrateModule.maxItems;
	}

	private int getTotal() {
		if(cachedTotal != -1)
			return cachedTotal;

		int items = 0;
		for(ItemStack stack : stacks)
			items += stack.getCount();

		cachedTotal = items;
		return items;
	}

	private void changeTotal(ItemStack oldStack, ItemStack newStack) {
		int diff = newStack.getCount() - oldStack.getCount();
		if(diff != 0)
			changeTotal(diff);
	}

	private void changeTotal(int change) {
		cachedTotal = getTotal() + change;
	}

	public void recalculate() {
		if(!needsUpdate)
			return;
		needsUpdate = false;

		displayTotal = 0;
		displaySlots = 0;

		NonNullList<ItemStack> newStacks = NonNullList.withSize(maxItems, ItemStack.EMPTY);
		int idx = 0;
		for(ItemStack stack : stacks) {
			if(!stack.isEmpty()) {
				newStacks.set(idx, stack);
				displayTotal += stack.getCount();
				displaySlots++;
				idx++;
			}
		}
		stacks = newStacks;
		cachedTotal = -1;
	}

	public void clear() {
		needsUpdate = false;
		stacks = NonNullList.withSize(maxItems, ItemStack.EMPTY);
		displayTotal = 0;
		displaySlots = 0;
	}

	public boolean isEmpty() {
		for(ItemStack stack : stacks)
			if(!stack.isEmpty())
				return false;
		return true;
	}

	public void spill(Level level, BlockPos worldPosition) {
		List<ItemStack> stacks = new ArrayList<>(this.stacks);
		SortingHandler.mergeStacks(stacks);

		for(ItemStack stack : stacks)
			if(!stack.isEmpty())
				Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
	}

	@Override
	public int getSlotLimit(int slot) {
		ItemStack stackInSlot = getStackInSlot(slot);
		int total = getTotal();
		return Mth.clamp(stackInSlot.getCount() + maxItems - total, 0, 64);
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		ItemStack oldStack = stacks.get(slot).copy();

		super.setStackInSlot(slot, stack);

		ItemStack newStack = stacks.get(slot).copy();
		changeTotal(oldStack, newStack);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		ItemStack oldStack = stacks.get(slot).copy();
		ItemStack retStack = super.insertItem(slot, stack, simulate);
		ItemStack newStack = stacks.get(slot).copy();

		if(!simulate)
			changeTotal(oldStack, newStack);

		return retStack;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack oldStack = stacks.get(slot).copy();
		ItemStack retStack = super.extractItem(slot, amount, simulate);
		ItemStack newStack = stacks.get(slot).copy();

		if(!simulate)
			changeTotal(oldStack, newStack);

		return retStack;
	}

	@Override
	public void onContentsChanged(int slot) {
		needsUpdate = true;
	}

	@Override
	protected void onLoad() {
		needsUpdate = true;
		recalculate();
	}

	@Override
	public CompoundTag serializeNBT() {
		ListTag items = new ListTag();
		for(ItemStack stack : stacks) {
			if(!stack.isEmpty())
				items.add(stack.save(new CompoundTag()));
		}
		CompoundTag nbt = new CompoundTag();
		nbt.put("stacks", items);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		stacks = NonNullList.withSize(maxItems, ItemStack.EMPTY);

		ListTag items = nbt.getList("stacks", Tag.TAG_COMPOUND);
		for(int i = 0; i < items.size(); i++)
			stacks.set(i, ItemStack.of(items.getCompound(i)));
		onLoad();
	}

}
