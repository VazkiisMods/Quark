package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.tools.item.PickarangItem;

@Mixin(DamageEnchantment.class)
public class DamageEnchantmentMixin {

	@ModifyReturnValue(method = "canEnchant", at = @At("RETURN"))
	private boolean canEnchant(boolean prev, ItemStack stack) {
		return prev || stack.getItem() instanceof PickarangItem;
	}
}
