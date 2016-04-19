/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [21/03/2016, 01:05:19 (GMT)]
 */
package vazkii.quark.vanity.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class FireworkCloningRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting var1, World var2) {
		boolean foundSource = false;
		boolean foundTarget = false;

		for(int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() instanceof ItemFirework) {
					if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Fireworks")) {
						if(foundSource)
							return false;
						foundSource = true;
					} else {
						if(foundTarget)
							return false;
						foundTarget = true;
					}
				}  else return false;
			}
		}

		return foundSource && foundTarget;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		ItemStack source = null;
		ItemStack target = null;

		for(int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if(stack != null) {
				if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Fireworks"))
					source = stack;
				else target = stack;
			}
		}

		ItemStack copy = target.copy();
		NBTTagCompound cmp = new NBTTagCompound();
		cmp.setTag("Fireworks", source.getTagCompound().getTag("Fireworks"));
		copy.setTagCompound(cmp);
		copy.stackSize = 1;

		return copy;
	}

	@Override
	public int getRecipeSize() {
		return 10;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		ItemStack[] remaining = new ItemStack[inv.getSizeInventory()];
		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null && stack.getTagCompound() != null && stack.getTagCompound().hasKey("Fireworks")) {
				ItemStack copy = stack.copy();
				copy.stackSize = 1;
				remaining[i] = copy;
			}
		}

		return remaining;
	}

}
