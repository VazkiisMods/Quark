package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.experimental.module.GameNerfsModule;

@Mixin(ElytraItem.class)
public class ElytraItemMixin {

	//TODO: IForgeItem
	@ModifyReturnValue(method = "canElytraFly", at = @At("RETURN"), remap = false)
	private boolean canApply(boolean prev, ItemStack stack, LivingEntity living) {
		return GameNerfsModule.canEntityUseElytra(living, prev);
	}

}
