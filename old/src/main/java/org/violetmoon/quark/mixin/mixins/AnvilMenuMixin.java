package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.experimental.module.EnchantmentsBegoneModule;
import org.violetmoon.quark.content.tweaks.module.DiamondRepairModule;

@Mixin(value = AnvilMenu.class, priority = 2000)
public class AnvilMenuMixin {

	@ModifyExpressionValue(
		method = "createResult()V", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/Item;isValidRepairItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
		),
		require = 0
	)
	public boolean isValidRepairItem(boolean isValid, @Local(ordinal = 1) ItemStack itemStack, @Local(ordinal = 2) ItemStack repairStack) {
		return DiamondRepairModule.isValidRepairItem(isValid, itemStack.getItem(), repairStack);
	}

	@WrapOperation(
		method = "createResult()V", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/enchantment/Enchantment;canEnchant(Lnet/minecraft/world/item/ItemStack;)Z"
		),
		require = 0
	)
	public boolean canEnchant(Enchantment enchantment, ItemStack stack, Operation<Boolean> original) {
		if(EnchantmentsBegoneModule.shouldBegone(enchantment))
			return false;
		return original.call(enchantment, stack);
	}

}
