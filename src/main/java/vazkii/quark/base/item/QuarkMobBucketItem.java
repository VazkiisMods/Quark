package vazkii.quark.base.item;

import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.material.Fluid;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class QuarkMobBucketItem extends MobBucketItem implements IQuarkItem {

	private final QuarkModule module;

	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkMobBucketItem(Supplier<? extends EntityType<?>> entity, Supplier<? extends Fluid> fluid, Supplier<? extends SoundEvent> sound, String name, QuarkModule module) {
		super(entity, fluid, sound, (new Properties()).stacksTo(1).tab(CreativeModeTab.TAB_MISC));

		Quark.ZETA.registry.registerItem(this, name);
		this.module = module;
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public QuarkMobBucketItem setCondition(BooleanSupplier enabledSupplier) {
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
