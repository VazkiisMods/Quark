package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.item.enchantment.ArrowPiercingEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArrowPiercingEnchantment.class)
public class ArrowPiercingEnchantmentMixin {

	@ModifyReturnValue(method = "checkCompatibility", at = @At("RETURN"))
	private boolean checkCompatibility(boolean compatible, Enchantment enchantment) {
		return compatible && enchantment != Enchantments.BLOCK_EFFICIENCY;
	}
}
