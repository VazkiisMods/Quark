package org.violetmoon.quark.content.building.client.render.entity;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.violetmoon.quark.content.building.entity.Stool;

import javax.annotation.Nonnull;

public class StoolEntityRenderer extends EntityRenderer<Stool> {

	public StoolEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull Stool entity) {
		return null;
	}

	@Override
	public boolean shouldRender(@Nonnull Stool livingEntityIn, @Nonnull Frustum camera, double camX, double camY, double camZ) {
		return false;
	}

}
