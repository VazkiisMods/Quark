package org.violetmoon.quark.mixin.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.violetmoon.quark.content.tools.module.ColorRunesModule;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {

	//TODO: Forge added method
	@Inject(method = "getArmorModelHook", at = @At("HEAD"), remap = false)
	private void quark$setColorRuneTargetStack(T entity, ItemStack itemStack, EquipmentSlot slot, A model, CallbackInfoReturnable<A> callbackInfoReturnable) {
		ColorRunesModule.setTargetStack(itemStack);
	}

	@Group(name = "quark_color_runes_mixin", min = 1, max = 1)
	@ModifyExpressionValue(method = "renderGlint(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/Model;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorEntityGlint()Lnet/minecraft/client/renderer/RenderType;"))
	private RenderType quark$getArmorGlint(RenderType prev) {
		return ColorRunesModule.Client.getArmorEntityGlint();
	}

	@Group(name = "quark_color_runes_mixin")
	@ModifyExpressionValue(method = "renderGlint(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/HumanoidModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorEntityGlint()Lnet/minecraft/client/renderer/RenderType;"))
	private RenderType quark$getArmorGlintOptifine(RenderType prev) {
		return ColorRunesModule.Client.getArmorEntityGlint();
	}
}
