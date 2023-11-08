package org.violetmoon.quark.base.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.zeta.item.IZetaItem;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public abstract class QuarkArrowItem extends ArrowItem implements IZetaItem {

	private final ZetaModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkArrowItem(String name, ZetaModule module) {
		super(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT));

		Quark.ZETA.registry.registerItem(this, name);
		this.module = module;
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public QuarkArrowItem setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public ZetaModule getModule() {
		return module;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}
	
	public static class Impl extends QuarkArrowItem {

		private final ArrowCreator creator;
		
		public Impl(String name, ZetaModule module, ArrowCreator creator) {
			super(name, module);
			this.creator = creator;
		}
		
		@Override
		public AbstractArrow createArrow(Level p_40513_, ItemStack p_40514_, LivingEntity p_40515_) {
			return creator.createArrow(p_40513_, p_40514_, p_40515_);
		}
		
		public static interface ArrowCreator {
			public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity living);
		}
		
	}
	
}
