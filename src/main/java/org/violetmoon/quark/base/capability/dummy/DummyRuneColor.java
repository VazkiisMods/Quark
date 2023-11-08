package org.violetmoon.quark.base.capability.dummy;

import net.minecraft.world.item.ItemStack;
import org.violetmoon.quark.api.IRuneColorProvider;

public class DummyRuneColor implements IRuneColorProvider {

	@Override
	public int getRuneColor(ItemStack stack) {
		return -1;
	}
}
