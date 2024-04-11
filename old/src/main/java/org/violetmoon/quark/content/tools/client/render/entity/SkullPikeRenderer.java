package org.violetmoon.quark.content.tools.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import org.jetbrains.annotations.NotNull;

import org.violetmoon.quark.content.tools.entity.SkullPike;

public class SkullPikeRenderer extends EntityRenderer<SkullPike> {

	public SkullPikeRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(@NotNull SkullPike entity, float yaw, float partialTicks, @NotNull PoseStack matrix, @NotNull MultiBufferSource buffer, int light) {}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(@NotNull SkullPike arg0) {
		return InventoryMenu.BLOCK_ATLAS;
	}

}
