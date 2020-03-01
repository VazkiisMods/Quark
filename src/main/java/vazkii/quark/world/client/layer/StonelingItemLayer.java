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
package vazkii.quark.world.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.world.client.model.StonelingModel;
import vazkii.quark.world.entity.FoxhoundEntity;
import vazkii.quark.world.entity.StonelingEntity;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class StonelingItemLayer extends LayerRenderer<StonelingEntity, StonelingModel> {

	public StonelingItemLayer(IEntityRenderer<StonelingEntity, StonelingModel> renderer) {
		super(renderer);
	}
	
	public void render(MatrixStack matrix, IRenderTypeBuffer buffer, int light, StonelingEntity stoneling,  float limbAngle, float limbDistance, float tickDelta, float customAngle, float headYaw, float headPitch) {
		ItemStack stack = stoneling.getCarryingItem();
		if (!stack.isEmpty()) {
			boolean isBlock = stack.getItem() instanceof BlockItem;
			
			matrix.push();
			matrix.translate(0F, 0.5F, 0F);
			if(!isBlock) {
				matrix.rotate(Vector3f.YP.rotationDegrees(stoneling.getItemAngle() + 180));
				matrix.rotate(Vector3f.XP.rotationDegrees(90F));
			} else matrix.rotate(Vector3f.XP.rotationDegrees(180F));
			
			matrix.scale(0.725F, 0.725F, 0.725F);
			Minecraft mc = Minecraft.getInstance();
			mc.getItemRenderer().renderItem(stack, TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrix, buffer);
			matrix.pop();
		}
	}

}
