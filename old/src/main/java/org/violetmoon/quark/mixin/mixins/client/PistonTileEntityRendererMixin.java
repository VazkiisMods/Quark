package org.violetmoon.quark.mixin.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.PistonHeadRenderer;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.violetmoon.quark.content.automation.client.render.QuarkPistonBlockEntityRenderer;

@Mixin(PistonHeadRenderer.class)
public class PistonTileEntityRendererMixin {

	@Inject(method = "render*", at = @At("HEAD"), cancellable = true)
	private void renderPistonBlock(PistonMovingBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, CallbackInfo callbackInfo) {
		if(QuarkPistonBlockEntityRenderer.renderPistonBlock(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn))
			callbackInfo.cancel();
	}
}
