package org.violetmoon.quark.mixin.mixins.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.violetmoon.quark.content.tweaks.client.emote.EmoteHandler;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends LivingEntity> {

	@Inject(method = "setupAnim*", at = @At("RETURN"))
	private void updateEmotes(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callbackInfo) {
		EmoteHandler.updateEmotes(entityIn);
	}
}
