package vazkii.quark.base.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.handler.RequiredModTooltipHandler;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public class QuarkItem extends Item implements IQuarkItem {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkItem(String regname, QuarkModule module, Properties properties) {
		super(properties);

		Quark.REGISTRY.registerItem(this, regname);
		this.module = module;

		if(module != null && module.category.isAddon())
			RequiredModTooltipHandler.map(this, module.category.requiredMod);
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public QuarkItem setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

}
