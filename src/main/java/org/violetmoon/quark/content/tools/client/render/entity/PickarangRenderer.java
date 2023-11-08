package org.violetmoon.quark.content.tools.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.violetmoon.quark.content.tools.entity.rang.AbstractPickarang;

import javax.annotation.Nonnull;

public class PickarangRenderer extends EntityRenderer<AbstractPickarang<?>> {

	public PickarangRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(AbstractPickarang<?> entity, float yaw, float partialTicks, PoseStack matrix, @Nonnull MultiBufferSource buffer, int light) {
		if (entity.tickCount >= 2) {
			matrix.pushPose();
			matrix.translate(0, 0.2, 0);
			matrix.mulPose(Vector3f.XP.rotationDegrees(90F));

			Minecraft mc = Minecraft.getInstance();
			float time = entity.tickCount + (mc.isPaused() ? 0 : partialTicks);
			matrix.mulPose(Vector3f.ZP.rotationDegrees(time * 20F));

			mc.getItemRenderer().renderStatic(entity.getStack(), TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrix, buffer, 0);

			matrix.popPose();
		}
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull AbstractPickarang<?> entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}

}
