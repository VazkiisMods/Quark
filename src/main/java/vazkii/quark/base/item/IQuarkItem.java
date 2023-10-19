package vazkii.quark.base.item;

import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import net.minecraft.world.item.Item;
import vazkii.zeta.module.ZetaModule;

public interface IQuarkItem {

	@Nullable
	ZetaModule getModule();

	default IQuarkItem setCondition(BooleanSupplier condition) {
		return this;
	}

	default boolean doesConditionApply() {
		return true;
	}
	
	default Item getItem() {
		return (Item) this;
	}

	default boolean isEnabled() {
		ZetaModule module = getModule();
		return module != null && module.enabled && doesConditionApply();
	}
	
}
