package vazkii.quark.base.block;

import java.util.function.BooleanSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.PressurePlateBlock;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.module.QuarkModule;

/**
 * @author WireSegal
 * Created at 9:41 PM on 10/8/19.
 */
public class QuarkPressurePlateBlock extends PressurePlateBlock implements IQuarkBlock {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkPressurePlateBlock(Sensitivity sensitivity, String regname, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(sensitivity, properties);
		this.module = module;

		Quark.ZETA.registry.registerBlock(this, regname, true);
		CreativeTabHandler.addTab(this, creativeTab);
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public QuarkPressurePlateBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public QuarkModule getModule() {
		return module;
	}

}
