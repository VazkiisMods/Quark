package vazkii.quark.mobs.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.mobs.entity.FoxhoundEntity;

/**
 * ModelFoxhound - McVinnyq
 * Created using Tabula 7.0.0
 */
public class FoxhoundModel extends EntityModel<FoxhoundEntity> {

	public final ModelRenderer head;
	public final ModelRenderer rightFrontLeg;
	public final ModelRenderer leftFrontLeg;
	public final ModelRenderer rightBackLeg;
	public final ModelRenderer leftBackLeg;
	public final ModelRenderer body;
	public final ModelRenderer snout;
	public final ModelRenderer rightEar;
	public final ModelRenderer leftEar;
	public final ModelRenderer tail;
	public final ModelRenderer fluff;
	
	private FoxhoundEntity entity;

	public FoxhoundModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.leftBackLeg = new ModelRenderer(this, 36, 32);
		this.leftBackLeg.setRotationPoint(3.0F, 12.0F, 9.5F);
		this.leftBackLeg.addBox(-1.5F, 0.0F, -1.5F, 3, 12, 3, 0.0F);
		this.rightFrontLeg = new ModelRenderer(this, 0, 32);
		this.rightFrontLeg.setRotationPoint(-2.0F, 12.0F, 2.0F);
		this.rightFrontLeg.addBox(-1.5F, 0.0F, -1.5F, 3, 12, 3, 0.0F);
		this.rightEar = new ModelRenderer(this, 0, 47);
		this.rightEar.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.rightEar.addBox(-4.0F, -5.0F, -5.0F, 2, 2, 3, 0.0F);
		this.tail = new ModelRenderer(this, 36, 16);
		this.tail.setRotationPoint(0.0F, 0.0F, 1.5F);
		this.tail.addBox(-2.0F, -4.0F, 0.0F, 4, 5, 10, 0.0F);
		this.setRotateAngle(tail, -1.3089969389957472F, 0.0F, 0.0F);
		this.body = new ModelRenderer(this, 0, 2);
		this.body.setRotationPoint(0.0F, 17.0F, 12.0F);
		this.body.addBox(-4.0F, -12.0F, 0.0F, 8, 12, 6, 0.0F);
		this.setRotateAngle(body, 1.5707963267948966F, 0.0F, 0.0F);
		this.fluff = new ModelRenderer(this, 28, 0);
		this.fluff.setRotationPoint(0.0F, -13.0F, 3.0F);
		this.fluff.addBox(-5.0F, 0.0F, -4.0F, 10, 8, 8, 0.05F);
		this.leftFrontLeg = new ModelRenderer(this, 12, 32);
		this.leftFrontLeg.setRotationPoint(2.0F, 12.0F, 2.0F);
		this.leftFrontLeg.addBox(-1.5F, 0.0F, -1.5F, 3, 12, 3, 0.0F);
		this.rightBackLeg = new ModelRenderer(this, 24, 32);
		this.rightBackLeg.setRotationPoint(-3.0F, 12.0F, 9.5F);
		this.rightBackLeg.addBox(-1.5F, 0.0F, -1.5F, 3, 12, 3, 0.0F);
		this.leftEar = new ModelRenderer(this, 10, 47);
		this.leftEar.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.leftEar.addBox(2.0F, -5.0F, -5.0F, 2, 2, 3, 0.0F);
		this.head = new ModelRenderer(this, 0, 20);
		this.head.setRotationPoint(0.0F, 14.5F, 0.0F);
		this.head.addBox(-4.0F, -3.0F, -6.0F, 8, 6, 6, 0.0F);
		this.snout = new ModelRenderer(this, 29, 18);
		this.snout.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.snout.addBox(-2.0F, 1.0F, -10.0F, 4, 2, 4, 0.0F);
		this.head.addChild(this.rightEar);
		this.body.addChild(this.tail);
		this.body.addChild(this.fluff);
		this.head.addChild(this.leftEar);
		this.head.addChild(this.snout);
	}

	@Override
	public void setLivingAnimations(FoxhoundEntity hound, float limbSwing, float limbSwingAmount, float partialTickTime) {
		this.entity = hound;
		if (hound.isSitting() || hound.isAngry())
			this.tail.rotateAngleX = -0.6544984695F;
		else
			this.tail.rotateAngleX = -1.3089969389957472F + MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;

		this.head.rotateAngleY = hound.getInterestedAngle(partialTickTime) - hound.getShakeAngle(partialTickTime, 0.0F);
		this.head.rotateAngleX = 0;
		this.body.rotateAngleY = hound.getShakeAngle(partialTickTime, -0.16F);
		this.tail.rotateAngleY = hound.getShakeAngle(partialTickTime, -0.2F);

		if (hound.isSleeping()) {
			this.head.setRotationPoint(1.0F, 20.5F, 0.0F);
			this.setRotateAngle(head, 0.0F, 0.7853981633974483F, -0.04363323129985824F);

			this.body.setRotationPoint(0.0F, 20.0F, 12.0F);
			this.setRotateAngle(body, 1.5707963267948966F, 0.0F, 1.5707963267948966F);
			this.tail.setRotationPoint(0.0F, -1.0F, 1.0F);
			this.setRotateAngle(tail, 2.5497515042385164F, -0.22759093446006054F, 0.0F);
			this.rightFrontLeg.setRotationPoint(0.0F, 18.0F, 2.0F);
			this.leftFrontLeg.setRotationPoint(2.0F, 21.0F, 1.0F);
			this.rightBackLeg.setRotationPoint(0.0F, 22.0F, 11.0F);
			this.leftBackLeg.setRotationPoint(3.0F, 20.0F, 10.0F);

			this.setRotateAngle(rightFrontLeg, 0.2181661564992912F, 0.4363323129985824F, 1.3089969389957472F);
			this.setRotateAngle(leftFrontLeg, 0.0F, 0.0F, 1.3962634015954636F);
			this.setRotateAngle(rightBackLeg, -1.0471975511965976F, -0.08726646259971647F, 1.48352986419518F);
			this.setRotateAngle(leftBackLeg, -0.7853981633974483F, 0.0F, 1.2217304763960306F);
		} else if (hound.isSitting()) {
			this.head.setRotationPoint(0.0F, 12.0F, 2.0F);
			this.body.setRotationPoint(0.0F, 23.0F, 7.0F);
			this.setRotateAngle(body, 0.7853981633974483F, this.body.rotateAngleY, 0F);
			this.tail.setRotationPoint(0.0F, 0.0F, -2.0F);
			this.setRotateAngle(tail, -0.5235987755982988F, -0.7243116395776468F, 0F);
			this.rightFrontLeg.setRotationPoint(-2.0F, 12.0F, 1.25F);
			this.leftFrontLeg.setRotationPoint(2.0F, 12.0F, 1.25F);
			this.rightBackLeg.setRotationPoint(-3.0F, 21.0F, 10.0F);
			this.leftBackLeg.setRotationPoint(3.0F, 21.0F, 10.0F);

			this.setRotateAngle(rightFrontLeg, 0F, 0F, 0F);
			this.setRotateAngle(leftFrontLeg, 0F, 0F, 0F);
			this.setRotateAngle(rightBackLeg, -1.3089969389957472F, 0.39269908169872414F, 0.0F);
			this.setRotateAngle(leftBackLeg, -1.3089969389957472F, -0.39269908169872414F, 0.0F);
		} else {
			this.head.setRotationPoint(0.0F, 14.5F, 0.0F);
			this.body.setRotationPoint(0.0F, 17.0F, 12.0F);
			this.setRotateAngle(body, 1.5707963267948966F, this.body.rotateAngleY, 0F);
			this.tail.setRotationPoint(0.0F, 0.0F, 1.5F);
			this.rightFrontLeg.setRotationPoint(-2.0F, 12.0F, 2.0F);
			this.leftFrontLeg.setRotationPoint(2.0F, 12.0F, 2.0F);
			this.rightBackLeg.setRotationPoint(-3.0F, 12.0F, 9.5F);
			this.leftBackLeg.setRotationPoint(3.0F, 12.0F, 9.5F);
			this.setRotateAngle(rightFrontLeg, MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount, 0, 0);
			this.setRotateAngle(leftFrontLeg, MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount, 0, 0);
			this.setRotateAngle(rightBackLeg, MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount, 0, 0);
			this.setRotateAngle(leftBackLeg, MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount, 0, 0);
		}
	}

	@Override
	public void setRotationAngles(FoxhoundEntity entity, float p_225597_2_, float p_225597_3_, float p_225597_4_, float yaw, float pitch) {
		if (!entity.isSleeping()) {
			head.rotateAngleY += yaw * 0.017453292F;
			head.rotateAngleX += pitch * 0.017453292F;
		} else
			head.rotateAngleY += MathHelper.cos(entity.ticksExisted / 30) / 20;    	
	}

	@Override
	public void render(MatrixStack matrix, IVertexBuilder vb, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
		matrix.push();
		matrix.translate(0, 0, entity.isSitting() ? -0.25F : -0.35F);

		matrix.push();
		
		if (isChild)
			matrix.translate(0.0F, 5.0F / 16F, 0F);

		head.render(matrix, vb, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
		
		matrix.pop();
		
		matrix.push();
		if (isChild) {
			matrix.translate(0.0F, 12.0F / 16F, 0F);
			matrix.scale(0.5F, 0.5F, 0.5F);
		}
		
		leftBackLeg.render(matrix, vb, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
		rightFrontLeg.render(matrix, vb, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
		body.render(matrix, vb, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
		leftFrontLeg.render(matrix, vb, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
		rightBackLeg.render(matrix, vb, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
		matrix.pop();
		matrix.pop();
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
