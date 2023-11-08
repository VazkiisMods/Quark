package org.violetmoon.zeta.module;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public interface IDisableable<SELF> {

	@Nullable ZetaModule getModule();

	SELF setCondition(BooleanSupplier condition);
	boolean doesConditionApply();

	default boolean isEnabled() {
		ZetaModule module = getModule();
		return module != null && module.enabled && doesConditionApply();
	}

	//Covers non-ZetaItems as well
	static boolean isEnabled(Item i) {
		if(i instanceof IDisableable<?> dis)
			return dis.isEnabled();
		else if(i instanceof BlockItem bi)
			return isEnabled(bi.getBlock());
		else
			return true;
	}

	//Covers non-ZetaBlocks as well
	@SuppressWarnings({"SimplifiableConditionalExpression", "PMD.SimplifiedTernary"}) //No u
	static boolean isEnabled(Block b) {
		return b instanceof IDisableable<?> dis ? dis.isEnabled() : true;
	}

}
