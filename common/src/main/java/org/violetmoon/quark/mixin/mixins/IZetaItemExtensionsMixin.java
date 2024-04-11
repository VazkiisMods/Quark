package org.violetmoon.quark.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.violetmoon.quark.mixin.delegates.ForgeItemDelegate;
import org.violetmoon.zeta.item.ext.IZetaItemExtensions;
import org.violetmoon.zeta.mixin.plugin.DelegateInterfaceMixin;
import org.violetmoon.zeta.mixin.plugin.DelegateReturnValueModifier;

@Mixin(IZetaItemExtensions.class)
@DelegateInterfaceMixin(
	delegate = ForgeItemDelegate.class, methods = {
			@DelegateReturnValueModifier(
				target = "getEnchantmentLevelZeta(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/Enchantment;)I",
				delegate = "getEnchantmentLevel", desc = "(ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/Enchantment;)I"
			),
			@DelegateReturnValueModifier(
				target = "getAllEnchantmentsZeta(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Map;",
				delegate = "getAllEnchantments", desc = "(Ljava/util/Map;Lnet/minecraft/world/item/ItemStack;)Ljava/util/Map;"
			)
	}
)
public interface IZetaItemExtensionsMixin {
	// Effective copy of IForgeItemMixin.
	// Implemented this way because Zeta should not depend on Quark code.
}
