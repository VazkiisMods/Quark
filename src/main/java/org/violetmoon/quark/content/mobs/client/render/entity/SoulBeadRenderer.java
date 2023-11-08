package org.violetmoon.quark.content.mobs.client.render.entity;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.violetmoon.quark.content.mobs.entity.SoulBead;

import javax.annotation.Nonnull;

public class SoulBeadRenderer extends EntityRenderer<SoulBead> {

	public SoulBeadRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull SoulBead entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}

	@Override
	public boolean shouldRender(@Nonnull SoulBead livingEntityIn, @Nonnull Frustum camera, double camX, double camY, double camZ) {
		return false;
	}

}
