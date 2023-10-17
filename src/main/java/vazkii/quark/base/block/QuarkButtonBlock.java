package vazkii.quark.base.block;

import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ButtonBlock;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

/**
 * @author WireSegal
 * Created at 9:14 PM on 10/8/19.
 */
public abstract class QuarkButtonBlock extends ButtonBlock implements IQuarkBlock {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkButtonBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(false, properties);
		this.module = module;

		Quark.ZETA.registry.registerBlock(this, regname, true);
		CreativeTabHandler.addTab(this, creativeTab);
	}

	@Nonnull
	@Override
	protected abstract SoundEvent getSound(boolean powered);

	@Override
	public abstract int getPressDuration();

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public QuarkButtonBlock setCondition(BooleanSupplier enabledSupplier) {
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
