package org.violetmoon.quark.base.block;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.CreativeModeTab;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;

public class QuarkWoodenButtonBlock extends QuarkButtonBlock {

	public QuarkWoodenButtonBlock(String regname, ZetaModule module, Properties properties) {
		super(regname, module, CreativeModeTab.TAB_REDSTONE, properties);
	}

	@Nonnull
	@Override
	protected SoundEvent getSound(boolean powered) {
		return powered ? SoundEvents.WOODEN_BUTTON_CLICK_ON : SoundEvents.WOODEN_BUTTON_CLICK_OFF;
	}

	@Override
	public int getPressDuration() {
		return 30;
	}

}
