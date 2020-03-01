package vazkii.quark.world.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.ModelRenderer.ModelBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.world.entity.StonelingEntity;

public class StonelingModel extends EntityModel<StonelingEntity> {

	private final ModelRenderer body;
	private final ModelRenderer arm_right;
	private final ModelRenderer arm_left;
	private final ModelRenderer leg_right;
	private final ModelRenderer leg_left;

	public StonelingModel() {
		textureWidth = 32;
		textureHeight = 32;

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 14.0F, 0.0F);

		ModelRenderer head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.addChild(head);
		
		// addBox = addCuboid
		
		head.addBox(null, -3.0F, -2.0F, -3.0F, 6, 8, 6, 0.0F, 0, 0);
		head.addBox(null, -1.0F, -4.0F, -5.0F, 2, 4, 2, 0.0F, 8, 24);
		head.addBox(null, -1.0F, 6.0F, -3.0F, 2, 2, 2, 0.0F, 16, 20);
		head.addBox(null, -1.0F, -4.0F, 3.0F, 2, 4, 2, 0.0F, 0, 24);
		head.addBox(null, -1.0F, -4.0F, -3.0F, 2, 2, 6, 0.0F, 16, 24);
		head.addBox(null, -1.0F, -4.0F, -1.0F, 2, 2, 2, 0.0F, 24, 20);
		head.addBox(null, -1.0F, 1.0F, -5.0F, 2, 2, 2, 0.0F, 18, 0);
		head.addBox(null, -4.0F, -1.0F, -3.0F, 1, 2, 2, 0.0F, 0, 0);
		head.addBox(null, 3.0F, -1.0F, -3.0F, 1, 2, 2, 0.0F, 0, 0);

		arm_right = new ModelRenderer(this);
		arm_right.setRotationPoint(-3.0F, 2.0F, 0.0F);
		setRotationAngle(arm_right, 3.1416F, 0.0F, 0.0F);
		body.addChild(arm_right);
		arm_right.addBox(null, -2.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, 0, 14);

		arm_left = new ModelRenderer(this);
		arm_left.setRotationPoint(3.0F, 2.0F, 0.0F);
		setRotationAngle(arm_left, 3.1416F, 0.0F, 0.0F);
		body.addChild(arm_left);
		arm_left.addBox(null, 0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, 8, 14);

		leg_right = new ModelRenderer(this);
		leg_right.setRotationPoint(-2.0F, 4.0F, 0.0F);
		body.addChild(leg_right);
		leg_right.addBox(null, -1.0F, 2.0F, -1.0F, 2, 4, 2, 0.0F, 16, 14);

		leg_left = new ModelRenderer(this);
		leg_left.setRotationPoint(1.0F, 4.0F, 0.0F);
		body.addChild(leg_left);
		leg_left.addBox(null, 0.0F, 2.0F, -1.0F, 2, 4, 2, 0.0F, 24, 14);
	}
	
	@Override
	public void setRotationAngles(StonelingEntity stoneling, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		leg_right.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
		leg_left.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;
		
		ItemStack carry = stoneling.getCarryingItem();
		if(carry.isEmpty() && !stoneling.isBeingRidden()) {
			arm_right.rotateAngleX = 0F;
			arm_left.rotateAngleX = 0F;
		} else {
			arm_right.rotateAngleX = 3.1416F;
			arm_left.rotateAngleX = 3.1416F;
		}
	}

	@Override
	public void render(MatrixStack matrix, IVertexBuilder vb, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
		body.render(matrix, vb, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
	}
	
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
