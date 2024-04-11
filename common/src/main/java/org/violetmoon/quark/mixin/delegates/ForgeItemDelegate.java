package org.violetmoon.quark.mixin.delegates;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.violetmoon.quark.content.tweaks.module.GoldToolsHaveFortuneModule;
import org.violetmoon.zeta.mixin.plugin.DelegateInterfaceTarget;
import org.violetmoon.zeta.mixin.plugin.DelegateReturnValueTarget;

import java.util.Map;

@DelegateInterfaceTarget
public class ForgeItemDelegate {
	@DelegateReturnValueTarget("getEnchantmentLevel(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/Enchantment;)I")
	public static int getEnchantmentLevel(int previous, ItemStack stack, Enchantment enchantment) {
		return GoldToolsHaveFortuneModule.getActualEnchantmentLevel(enchantment, stack, previous);
	}

	@DelegateReturnValueTarget("getAllEnchantments(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Map;")
	public static Map<Enchantment, Integer> getAllEnchantments(Map<Enchantment, Integer> previous, ItemStack stack) {
		GoldToolsHaveFortuneModule.addEnchantmentsIfMissing(stack, previous);
		return previous;
	}
}
