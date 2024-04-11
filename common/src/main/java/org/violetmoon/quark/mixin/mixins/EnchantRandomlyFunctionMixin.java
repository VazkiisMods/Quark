package org.violetmoon.quark.mixin.mixins;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import org.violetmoon.quark.content.experimental.module.EnchantmentsBegoneModule;

import java.util.List;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {

	@ModifyVariable(method = "run", at = @At("STORE"))
	private List<Enchantment> filterBegoneFromTrades(List<Enchantment> enchantments) {
		return EnchantmentsBegoneModule.begoneEnchantments(enchantments);
	}
}
