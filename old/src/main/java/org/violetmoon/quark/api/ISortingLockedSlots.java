package org.violetmoon.quark.api;

import org.jetbrains.annotations.Nullable;

/**
 * Implement in a Menu to provide slots that shouldn't be sorted
 */
public interface ISortingLockedSlots extends IQuarkButtonAllowed {

	@Nullable
	public int[] getSortingLockedSlots(boolean sortingPlayerInventory);

}
