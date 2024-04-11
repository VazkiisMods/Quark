package org.violetmoon.quark.content.mobs.client.render.entity;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import org.jetbrains.annotations.NotNull;

import org.violetmoon.quark.content.mobs.entity.SoulBead;

public class SoulBeadRenderer extends EntityRenderer<SoulBead> {

	public SoulBeadRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(@NotNull SoulBead entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}

	@Override
	public boolean shouldRender(@NotNull SoulBead livingEntityIn, @NotNull Frustum camera, double camX, double camY, double camZ) {
		return false;
	}

}
