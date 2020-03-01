/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 13, 2019, 13:31 AM (EST)]
 */
package vazkii.quark.world.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.base.Quark;
import vazkii.quark.world.client.model.FoxhoundModel;
import vazkii.quark.world.entity.FoxhoundEntity;

public class FoxhoundCollarLayer extends LayerRenderer<FoxhoundEntity, FoxhoundModel> {

	private static final ResourceLocation WOLF_COLLAR = new ResourceLocation(Quark.MOD_ID, "textures/model/entity/foxhound_collar.png");

	public FoxhoundCollarLayer(IEntityRenderer<FoxhoundEntity, FoxhoundModel> renderer) {
		super(renderer);
	}

	@Override
	public void render(MatrixStack matrix, IRenderTypeBuffer buffer, int light, FoxhoundEntity foxhound,  float limbAngle, float limbDistance, float tickDelta, float customAngle, float headYaw, float headPitch) {
		if (foxhound.isTamed() && !foxhound.isInvisible()) {
			float[] afloat = foxhound.getCollarColor().getColorComponentValues();
			renderCutoutModel(getEntityModel(), WOLF_COLLAR, matrix, buffer, light, foxhound, afloat[0], afloat[1], afloat[2]);
		}
	}

}
