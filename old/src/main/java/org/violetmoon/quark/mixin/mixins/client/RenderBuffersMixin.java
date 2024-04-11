package org.violetmoon.quark.mixin.mixins.client;

import com.mojang.blaze3d.vertex.BufferBuilder;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.violetmoon.quark.content.tools.client.render.GlintRenderTypes;

@Mixin(RenderBuffers.class)
public class RenderBuffersMixin {

	@Inject(method = "put", at = @At("HEAD"))
	private static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> mapBuildersIn, RenderType renderTypeIn, CallbackInfo callbackInfo) {
		GlintRenderTypes.addGlintTypes(mapBuildersIn);
	}
}
