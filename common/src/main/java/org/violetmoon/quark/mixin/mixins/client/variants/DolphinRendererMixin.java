package org.violetmoon.quark.mixin.mixins.client.variants;

import net.minecraft.client.renderer.entity.DolphinRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Dolphin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.violetmoon.quark.content.client.module.VariantAnimalTexturesModule;

@Mixin(DolphinRenderer.class)
public class DolphinRendererMixin {
	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Dolphin;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
	private void overrideTexture(Dolphin dolphin, CallbackInfoReturnable<ResourceLocation> cir) {
		ResourceLocation loc = VariantAnimalTexturesModule.Client.getDolphinTexture(dolphin);
		if(loc != null)
			cir.setReturnValue(loc);
	}
}
