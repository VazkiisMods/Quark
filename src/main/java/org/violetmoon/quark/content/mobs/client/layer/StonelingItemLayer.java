/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 11, 2019, 16:46 AM (EST)]
 */
package org.violetmoon.quark.content.mobs.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.violetmoon.quark.content.mobs.client.model.StonelingModel;
import org.violetmoon.quark.content.mobs.entity.Stoneling;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class StonelingItemLayer extends RenderLayer<Stoneling, StonelingModel> {

	public StonelingItemLayer(RenderLayerParent<Stoneling, StonelingModel> renderer) {
		super(renderer);
	}

	@Override
	public void render(@Nonnull PoseStack matrix, @Nonnull MultiBufferSource buffer, int light, Stoneling stoneling, float limbAngle, float limbDistance, float tickDelta, float customAngle, float headYaw, float headPitch) {
		ItemStack stack = stoneling.getCarryingItem();
		if (!stack.isEmpty()) {
			boolean isBlock = stack.getItem() instanceof BlockItem;

			matrix.pushPose();

			matrix.translate(0F, 0.515F, 0F);

			if(!isBlock) {
				matrix.mulPose(Vector3f.YP.rotationDegrees(stoneling.getItemAngle() + 180));
				matrix.mulPose(Vector3f.XP.rotationDegrees(90F));
			} else matrix.mulPose(Vector3f.XP.rotationDegrees(180F));

			float scale = 0.8F;
			matrix.scale(scale, scale, scale);
			Minecraft mc = Minecraft.getInstance();
			mc.getItemRenderer().renderStatic(stack, TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrix, buffer, 0);
			matrix.popPose();
		}
	}

}
