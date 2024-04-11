package org.violetmoon.quark.mixin.mixins.client.variants;

import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.violetmoon.quark.content.client.module.VariantAnimalTexturesModule;

@Mixin(PigRenderer.class)
public class PigRendererMixin {
	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Pig;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
	private void overrideTexture(Pig pig, CallbackInfoReturnable<ResourceLocation> cir) {
		ResourceLocation loc = VariantAnimalTexturesModule.Client.getPigTexture(pig);
		if(loc != null)
			cir.setReturnValue(loc);
	}
}
