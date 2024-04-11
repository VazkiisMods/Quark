package org.violetmoon.quark.mixin.mixins.client.variants;

import net.minecraft.client.renderer.entity.LlamaRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Llama;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.violetmoon.quark.content.client.module.VariantAnimalTexturesModule;

@Mixin(LlamaRenderer.class)
public class LlamaRendererMixin {
	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/horse/Llama;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
	private void overrideTexture(Llama llama, CallbackInfoReturnable<ResourceLocation> cir) {
		ResourceLocation loc = VariantAnimalTexturesModule.Client.getLlamaTexture(llama);
		if(loc != null)
			cir.setReturnValue(loc);
	}
}
