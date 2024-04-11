package org.violetmoon.quark.mixin.mixins.accessor;

import net.minecraft.world.item.trading.MerchantOffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MerchantOffer.class)
public interface AccessorMerchantOffer {

	@Accessor("rewardExp")
	void quark$setRewardExp(boolean rewardExp);
}
