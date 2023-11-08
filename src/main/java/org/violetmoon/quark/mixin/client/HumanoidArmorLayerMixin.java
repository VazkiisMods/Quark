package org.violetmoon.quark.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.quark.content.tools.module.ColorRunesModule;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {

	@Inject(method = "getArmorModelHook", at = @At("HEAD"), remap = false)
	private void setColorRuneTargetStack(T entity, ItemStack itemStack, EquipmentSlot slot, A model, CallbackInfoReturnable<A> callbackInfoReturnable) {
		ColorRunesModule.setTargetStack(itemStack);
	}
}
