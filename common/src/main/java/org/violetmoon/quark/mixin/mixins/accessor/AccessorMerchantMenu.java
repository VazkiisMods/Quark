package org.violetmoon.quark.mixin.mixins.accessor;

import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MerchantMenu.class)
public interface AccessorMerchantMenu {
    @Invoker("moveFromInventoryToPaymentSlot")
    void quark$moveFromInventoryToPaymentSlot(int paymentSlotIndex, ItemStack paymentSlot);
}
