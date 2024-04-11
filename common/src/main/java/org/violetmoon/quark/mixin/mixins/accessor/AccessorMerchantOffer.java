package org.violetmoon.quark.mixin.mixins.accessor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MerchantOffer.class)
public interface AccessorMerchantOffer {

	@Accessor("rewardExp")
	void quark$setRewardExp(boolean rewardExp);

	@Invoker("isRequiredItem")
	boolean quark$isRequiredItem(ItemStack offer, ItemStack cost);
}
