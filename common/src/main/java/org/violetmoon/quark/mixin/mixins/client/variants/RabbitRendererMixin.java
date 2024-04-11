package org.violetmoon.quark.mixin.mixins.client.variants;

import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Rabbit;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.violetmoon.quark.content.client.module.VariantAnimalTexturesModule;

@Mixin(RabbitRenderer.class)
public class RabbitRendererMixin {
	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Rabbit;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
	private void overrideTexture(Rabbit rabbit, CallbackInfoReturnable<ResourceLocation> cir) {
		ResourceLocation loc = VariantAnimalTexturesModule.Client.getRabbitTexture(rabbit);
		if(loc != null)
			cir.setReturnValue(loc);
	}
}
