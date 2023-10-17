package vazkii.quark.base.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class QuarkSpawnEggItem extends ForgeSpawnEggItem implements IQuarkItem {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkSpawnEggItem(Supplier<EntityType<? extends Mob>> type, int primaryColor, int secondaryColor, String regname, QuarkModule module, Properties properties) {
		super(type, primaryColor, secondaryColor, properties);

		Quark.ZETA.registry.registerItem(this, regname);
		this.module = module;
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public QuarkSpawnEggItem setCondition(BooleanSupplier enabledSupplier) {
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
