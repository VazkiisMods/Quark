package vazkii.quark.world.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.world.entity.CrabEntity;

public class CrabModel extends EntityModel<CrabEntity> {

	private float wiggleX = 0;
	private float wiggleY = 0;
	private float crabSize = 0;
	
	public ModelRenderer group;

	public ModelRenderer body;
	public ModelRenderer rightClaw;
	public ModelRenderer leftClaw;
	public ModelRenderer rightLeg1;
	public ModelRenderer rightLeg2;
	public ModelRenderer rightLeg3;
	public ModelRenderer rightLeg4;
	public ModelRenderer leftLeg1;
	public ModelRenderer leftLeg2;
	public ModelRenderer leftLeg3;
	public ModelRenderer leftLeg4;
	public ModelRenderer rightEye;
	public ModelRenderer leftEye;

	private final Set<ModelRenderer> leftLegs;
	private final Set<ModelRenderer> rightLegs;

	public CrabModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;

		group = new ModelRenderer(this);
		group.setRotationPoint(0.0F, 0.0F, 0.0F);

		this.leftLeg4 = new ModelRenderer(this, 0, 19);
		this.leftLeg4.mirror = true;
		this.leftLeg4.setRotationPoint(3.0F, 20.0F, -1.0F);
		this.leftLeg4.addBox(0.0F, -0.5F, -0.5F, 6, 1, 1, 0.0F);
		this.setRotateAngle(leftLeg4, 0.0F, 0.4363323129985824F, 0.7853981633974483F);
		this.leftLeg3 = new ModelRenderer(this, 0, 19);
		this.leftLeg3.mirror = true;
		this.leftLeg3.setRotationPoint(3.0F, 20.0F, 0.0F);
		this.leftLeg3.addBox(0.0F, -0.5F, -0.5F, 6, 1, 1, 0.0F);
		this.setRotateAngle(leftLeg3, 0.0F, 0.2181661564992912F, 0.7853981633974483F);
		this.rightEye = new ModelRenderer(this, 0, 11);
		this.rightEye.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.rightEye.addBox(-3.0F, -3.5F, -2.85F, 1, 3, 1, 0.0F);
		this.setRotateAngle(rightEye, -0.39269908169872414F, 0.0F, 0.0F);
		this.rightLeg4 = new ModelRenderer(this, 0, 19);
		this.rightLeg4.setRotationPoint(-3.0F, 20.0F, -1.0F);
		this.rightLeg4.addBox(-6.0F, -0.5F, -0.5F, 6, 1, 1, 0.0F);
		this.setRotateAngle(rightLeg4, 0.0F, -0.4363323129985824F, -0.7853981633974483F);
		this.rightClaw = new ModelRenderer(this, 14, 11);
		this.rightClaw.setRotationPoint(-3.0F, 20.0F, -4.0F);
		this.rightClaw.addBox(-3.0F, -2.5F, -6.0F, 3, 5, 6, 0.0F);
		this.setRotateAngle(rightClaw, 0.0F, 0.39269908169872414F, -0.39269908169872414F);
		this.leftLeg1 = new ModelRenderer(this, 0, 19);
		this.leftLeg1.mirror = true;
		this.leftLeg1.setRotationPoint(3.0F, 20.0F, 2.0F);
		this.leftLeg1.addBox(0.0F, -0.5F, -0.5F, 6, 1, 1, 0.0F);
		this.setRotateAngle(leftLeg1, 0.0F, -0.4363323129985824F, 0.7853981633974483F);
		this.rightLeg2 = new ModelRenderer(this, 0, 19);
		this.rightLeg2.setRotationPoint(-3.0F, 20.0F, 0.9F);
		this.rightLeg2.addBox(-6.0F, -0.5F, -0.5F, 6, 1, 1, 0.0F);
		this.setRotateAngle(rightLeg2, 0.0F, 0.2181661564992912F, -0.7853981633974483F);
		this.leftClaw = new ModelRenderer(this, 14, 11);
		this.leftClaw.mirror = true;
		this.leftClaw.setRotationPoint(3.0F, 20.0F, -4.0F);
		this.leftClaw.addBox(0.0F, -2.5F, -6.0F, 3, 5, 6, 0.0F);
		this.setRotateAngle(leftClaw, 0.0F, -0.39269908169872414F, 0.39269908169872414F);
		this.rightLeg1 = new ModelRenderer(this, 0, 19);
		this.rightLeg1.setRotationPoint(-3.0F, 20.0F, 2.0F);
		this.rightLeg1.addBox(-6.0F, -0.5F, -0.5F, 6, 1, 1, 0.0F);
		this.setRotateAngle(rightLeg1, 0.0F, 0.4363323129985824F, -0.7853981633974483F);
		this.body = new ModelRenderer(this, 0, 0);
		this.body.setRotationPoint(0.0F, 20.0F, 0.0F);
		this.body.addBox(-4.0F, -2.5F, -3.0F, 8, 5, 6, 0.0F);
		this.leftEye = new ModelRenderer(this, 0, 11);
		this.leftEye.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.leftEye.addBox(2.0F, -3.5F, -2.85F, 1, 3, 1, 0.0F);
		this.setRotateAngle(leftEye, -0.39269908169872414F, 0.0F, 0.0F);
		this.leftLeg2 = new ModelRenderer(this, 0, 19);
		this.leftLeg2.mirror = true;
		this.leftLeg2.setRotationPoint(3.0F, 20.0F, 0.9F);
		this.leftLeg2.addBox(0.0F, -0.5F, -0.5F, 6, 1, 1, 0.0F);
		this.setRotateAngle(leftLeg2, 0.0F, -0.2181661564992912F, 0.7853981633974483F);
		this.rightLeg3 = new ModelRenderer(this, 0, 19);
		this.rightLeg3.setRotationPoint(-3.0F, 20.0F, 0.0F);
		this.rightLeg3.addBox(-6.0F, -0.5F, -0.5F, 6, 1, 1, 0.0F);
		this.setRotateAngle(rightLeg3, 0.0F, -0.2181661564992912F, -0.7853981633974483F);
		this.body.addChild(this.rightEye);
		this.body.addChild(this.leftEye);

		this.group.addChild(body);
		this.group.addChild(rightLeg1);
		this.group.addChild(rightLeg2);
		this.group.addChild(rightLeg3);
		this.group.addChild(rightLeg4);
		this.group.addChild(leftLeg1);
		this.group.addChild(leftLeg2);
		this.group.addChild(leftLeg3);
		this.group.addChild(leftLeg4);
		this.group.addChild(rightClaw);
		this.group.addChild(leftClaw);

		leftLegs = ImmutableSet.of(leftLeg1, leftLeg2, leftLeg3, leftLeg4);
		rightLegs = ImmutableSet.of(rightLeg1, rightLeg2, rightLeg3, rightLeg4);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(CrabEntity crab, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		rightLeg1.rotateAngleZ = -0.2618F + (-1 + MathHelper.cos(limbSwing * 0.6662F)) * 0.7F * limbSwingAmount;
		rightLeg2.rotateAngleZ = -0.5236F + (-1 + MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI)) * 0.7F * limbSwingAmount;
		rightLeg3.rotateAngleZ = -0.5236F + (-1 + MathHelper.cos(limbSwing * 0.6662F)) * 0.7F * limbSwingAmount;
		rightLeg4.rotateAngleZ = -0.2618F + (-1 + MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI)) * 0.7F * limbSwingAmount;
		leftLeg1.rotateAngleZ = 0.2618F + (1 + MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI)) * 0.7F * limbSwingAmount;
		leftLeg2.rotateAngleZ = 0.5236F + (1 + MathHelper.cos(limbSwing * 0.6662F)) * 0.7F * limbSwingAmount;
		leftLeg3.rotateAngleZ = 0.5236F + (1 + MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI)) * 0.7F * limbSwingAmount;
		leftLeg4.rotateAngleZ = 0.2618F + (1 + MathHelper.cos(limbSwing * 0.6662F)) * 0.7F * limbSwingAmount;

		leftClaw.rotateAngleX = 0.0f;
		rightClaw.rotateAngleX = 0.0f;
		wiggleX = 0.0f;
		wiggleY = 0.0f;
		
		crabSize = crab.getSizeModifier();
		if(isChild) 
			crabSize /= 2;

		if(crab.isRaving()) {
			float crabRaveBPM = 125F / 4;
			float freq = (20F / crabRaveBPM);
			float tick = ageInTicks * freq;
			float sin = (float) (Math.sin(tick) * 0.5 + 0.5);
			
			float legRot = (sin * 0.8F) + 0.6F;
			leftLegs.forEach(l -> l.rotateAngleZ = legRot);
			rightLegs.forEach(l -> l.rotateAngleZ = -legRot);
			
			float maxHeight = -0.05F;
			float horizontalOff = 0.2F;
			wiggleX = (sin - 0.5F) * 2 * maxHeight + maxHeight / 2;
			
			float slowSin = (float) Math.sin(tick / 2);
			wiggleY = slowSin * horizontalOff;
			
			float armRot = sin * 0.5F - 1.2F;
			leftClaw.rotateAngleX = armRot;
			rightClaw.rotateAngleX = armRot;
		}
	}
	
	
	public void render(MatrixStack matrix, IVertexBuilder vb, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
		matrix.push();
		matrix.translate(0, 1.5 - crabSize * 1.5, 0);
		matrix.scale(crabSize, crabSize, crabSize);
		matrix.rotate(Vector3f.YP.rotationDegrees(90F));
		matrix.translate(wiggleX, wiggleY, 0);
		group.render(matrix, vb, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
		matrix.pop();
	}

	
}
