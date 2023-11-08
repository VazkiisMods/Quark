package org.violetmoon.quark.content.mobs.client.layer.shiba;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import org.violetmoon.quark.content.mobs.client.model.ShibaModel;
import org.violetmoon.quark.content.mobs.entity.Shiba;

import javax.annotation.Nonnull;

public class ShibaMouthItemLayer extends RenderLayer<Shiba, ShibaModel> {

	private final ItemInHandRenderer itemInHandRenderer;

	public ShibaMouthItemLayer(RenderLayerParent<Shiba, ShibaModel> model, ItemInHandRenderer itemInHandRenderer) {
		super(model);
		this.itemInHandRenderer = itemInHandRenderer;
	}

	@Override
	public void render(@Nonnull PoseStack matrix, @Nonnull MultiBufferSource bufferIn, int packedLightIn, Shiba entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		ItemStack item = entitylivingbaseIn.getMouthItem();
		if(item.isEmpty())
			return;

		boolean sword = item.getItem() instanceof SwordItem;
		boolean trident = item.getItem() instanceof TridentItem;
		float scale = sword || trident ? 0.75F : 0.5F;
		matrix.pushPose();
		getParentModel().transformToHead(matrix);

		if(sword)
			matrix.translate(0.3, -0.15, -0.5);
		else if(trident) {
			matrix.translate(1, -0.6, -0.7);
			matrix.mulPose(Vector3f.YP.rotationDegrees(40F));
		} else
			matrix.translate(0, -0.15, -0.5);
		matrix.scale(scale, scale, scale);

		matrix.mulPose(Vector3f.YP.rotationDegrees(45));
		matrix.mulPose(Vector3f.XP.rotationDegrees(90));
		itemInHandRenderer.renderItem(entitylivingbaseIn, item, ItemTransforms.TransformType.NONE, true, matrix, bufferIn, packedLightIn);
		matrix.popPose();
	}
}
