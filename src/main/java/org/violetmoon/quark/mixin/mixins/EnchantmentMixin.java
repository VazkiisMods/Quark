package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.experimental.module.EnchantmentsBegoneModule;
import org.violetmoon.quark.content.tools.item.PickarangItem;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

	@ModifyReturnValue(method = "canEnchant", at = @At("RETURN"))
	private boolean canApply(boolean prev, ItemStack stack) {
		Enchantment self = (Enchantment) (Object) this;
		return !EnchantmentsBegoneModule.shouldBegone(self);
	}
}
