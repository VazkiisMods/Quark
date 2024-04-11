package org.violetmoon.quark.api;

import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public interface ICustomSorting {

	/**
	 * Gets a comparator to compare the items.
	 */
	Comparator<ItemStack> getItemComparator();

	/**
	 * Gets this item's category. Items will only be compared together if they are in the same category.
	 * Make sure your category is unique. Prefixing it with the mod ID is a good idea.
	 */
	String getSortingCategory();

}
