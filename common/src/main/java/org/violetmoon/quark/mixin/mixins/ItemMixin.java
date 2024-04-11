package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.management.module.ExpandedItemInteractionsModule;

@Mixin(Item.class)
public class ItemMixin {

	@ModifyReturnValue(method = "overrideStackedOnOther", at = @At("RETURN"))
	public boolean overrideStackedOnOther(boolean prev, ItemStack stack, Slot slot, ClickAction action, Player player) {
		return prev || ExpandedItemInteractionsModule.overrideStackedOnOther(stack, slot, action, player);
	}

	@ModifyReturnValue(method = "overrideOtherStackedOnMe", at = @At("RETURN"))
	public boolean overrideOtherStackedOnMe(boolean prev, ItemStack stack, ItemStack incoming, Slot slot, ClickAction action, Player player, SlotAccess accessor) {
		return prev || ExpandedItemInteractionsModule.overrideOtherStackedOnMe(stack, incoming, slot, action, player, accessor);
	}

}
