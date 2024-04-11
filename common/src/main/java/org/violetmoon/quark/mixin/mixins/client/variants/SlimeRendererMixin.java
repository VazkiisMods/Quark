package org.violetmoon.quark.mixin.mixins.client.variants;

import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Slime;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.violetmoon.quark.content.client.module.VariantAnimalTexturesModule;

@Mixin(SlimeRenderer.class)
public class SlimeRendererMixin {

	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/monster/Slime;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
	private void overrideTexture(Slime slime, CallbackInfoReturnable<ResourceLocation> cir) {
		ResourceLocation loc = VariantAnimalTexturesModule.Client.getSlimeTexture(slime);
		if(loc != null)
			cir.setReturnValue(loc);
	}
}
