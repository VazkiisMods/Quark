/*
 * The Cool MIT License (CMIT)
 *
 * Copyright (c) 2023 Emi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, as long as the person is cool, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * The person is cool.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.violetmoon.quark.content.automation.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.violetmoon.quark.content.automation.block.be.CrafterBlockEntity;
import org.violetmoon.quark.content.automation.module.CrafterModule;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class CrafterMenu extends AbstractContainerMenu {
	public final Container crafter;
	public final ContainerData delegate;

	private final ContainerLevelAccess access;

	public CrafterMenu(int syncId, Inventory player) {
		this(syncId, player, (it) -> new TransientCraftingContainer(it, 3, 3), new ResultContainer(), new SimpleContainerData(1), ContainerLevelAccess.NULL);
	}

	public CrafterMenu(int syncId, Inventory player, Function<CrafterMenu, CraftingContainer> crafterProvider, ResultContainer result, ContainerData delegate, ContainerLevelAccess access) {
		super(CrafterModule.menuType, syncId);

		CraftingContainer crafter = crafterProvider.apply(this);

		this.access = access;

		checkContainerSize(crafter, 9);
		this.crafter = crafter;
		crafter.startOpen(player.player);
		this.delegate = delegate;
		this.addDataSlots(delegate);

		this.addSlot(new ResultSlot(player.player, crafter, result, 0, 26 + 18 * 6, 17 + 18) {
			@Override
			public boolean mayPickup(Player p_40228_) {
				return false;
			}
		});

		int i;
		int j;
		for(i = 0; i < 3; ++i) {
			for(j = 0; j < 3; ++j) {
				int index = j + i * 3;
				this.addSlot(new Slot(crafter, index, 26 + j * 18, 17 + i * 18) {
					@Override
					public boolean mayPlace(@Nonnull ItemStack stack) {
						return (delegate.get(0) & (1 << index)) == 0;
					}
				});
			}
		}

		for(i = 0; i < 3; ++i) {
			for(j = 0; j < 9; ++j) {
				this.addSlot(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for(i = 0; i < 9; ++i) {
			this.addSlot(new Slot(player, i, 8 + i * 18, 142));
		}
	}

	public static CrafterMenu fromNetwork(int windowId, Inventory playerInventory, FriendlyByteBuf buf) {
		return new CrafterMenu(windowId, playerInventory);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (id >= 0 && id < 9) {
			access.execute((level, pos) -> {
				if (!level.isClientSide) {
					BlockEntity be = level.getBlockEntity(pos);
					if (be instanceof CrafterBlockEntity cbe) {
						cbe.blocked[id] = !cbe.blocked[id];
						cbe.update();
						cbe.setChanged();
					}
				}
			});
			return true;
		}

		return super.clickMenuButton(player, id);
	}

	@Override
	public void slotsChanged(Container container) {
		super.slotsChanged(container);
		access.execute((level, pos) -> {
			if (!level.isClientSide) {
				BlockEntity be = level.getBlockEntity(pos);
				if (be instanceof CrafterBlockEntity cbe) {
					cbe.update();
				}
			}
		});
	}

	@Override
	public boolean stillValid(Player player) {
		return crafter.stillValid(player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = this.getSlot(index);
		ItemStack original = ItemStack.EMPTY;
		if (slot != null && slot.hasItem()) {
			ItemStack stack = slot.getItem();
			original = stack.copy();
			if (index == 0) {
				if (!this.moveItemStackTo(stack, 10, 46, true)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(stack, original);
				/*
				this.context.run((world, pos) -> {
					stack.getItem().onCraft(stack, world, player);
				});*/
			} else if (index < 10) {
				if (!this.moveItemStackTo(stack, 10, 46, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (!this.moveItemStackTo(stack, 1, 10, false)) {
					if (index < 37) {
						if (!this.moveItemStackTo(stack, 37, 46, false)) {
							return ItemStack.EMPTY;
						}
					} else if (!this.moveItemStackTo(stack, 10, 37, false)) {
						return ItemStack.EMPTY;
					}
				}
			}
			if (stack.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			if (stack.getCount() == original.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, stack);
			if (index == 0) {
				player.drop(stack, false);
			}
		}
		return original;
	}
}
