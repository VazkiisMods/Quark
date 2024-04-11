package org.violetmoon.quark.content.mobs.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import org.violetmoon.quark.base.client.handler.ModelHandler;
import org.violetmoon.quark.content.mobs.client.layer.StonelingItemLayer;
import org.violetmoon.quark.content.mobs.client.layer.StonelingLichenLayer;
import org.violetmoon.quark.content.mobs.client.model.StonelingModel;
import org.violetmoon.quark.content.mobs.entity.Stoneling;

public class StonelingRenderer extends MobRenderer<Stoneling, StonelingModel> {

	public StonelingRenderer(EntityRendererProvider.Context context) {
		super(context, ModelHandler.model(ModelHandler.stoneling), 0.3F);
		addLayer(new StonelingItemLayer(this));
		addLayer(new StonelingLichenLayer(this));
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(@NotNull Stoneling entity) {
		return entity.getVariant().getTexture();
	}

}
