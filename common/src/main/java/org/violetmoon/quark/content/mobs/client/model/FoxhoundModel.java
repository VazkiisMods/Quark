package org.violetmoon.quark.content.mobs.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import org.jetbrains.annotations.NotNull;

import org.violetmoon.quark.content.mobs.entity.Foxhound;

/**
 * ModelFoxhound - McVinnyq
 * Created using Tabula 7.0.0
 */
public class FoxhoundModel extends EntityModel<Foxhound> {

	public final ModelPart head;
	public final ModelPart rightFrontLeg;
	public final ModelPart leftFrontLeg;
	public final ModelPart rightBackLeg;
	public final ModelPart leftBackLeg;
	public final ModelPart body;
	public final ModelPart snout;
	public final ModelPart rightEar;
	public final ModelPart leftEar;
	public final ModelPart tail;
	public final ModelPart fluff;

	private Foxhound entity;

	public FoxhoundModel(ModelPart root) {
		head = root.getChild("head");
		rightFrontLeg = root.getChild("rightFrontLeg");
		leftFrontLeg = root.getChild("leftFrontLeg");
		rightBackLeg = root.getChild("rightBackLeg");
		leftBackLeg = root.getChild("leftBackLeg");
		body = root.getChild("body");
		snout = head.getChild("snout");
		rightEar = head.getChild("rightEar");
		leftEar = head.getChild("leftEar");
		tail = body.getChild("tail");
		fluff = body.getChild("fluff");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		PartDefinition head = root.addOrReplaceChild("head",
				CubeListBuilder.create()
						.texOffs(0, 20)
						.addBox(-4.0F, -3.0F, -6.0F, 8, 6, 6),
				PartPose.offsetAndRotation(0.0F, 14.5F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body",
				CubeListBuilder.create()
						.texOffs(0, 2)
						.addBox(-4.0F, -12.0F, 0.0F, 8, 12, 6),
				PartPose.offsetAndRotation(0.0F, 17.0F, 12.0F, 1.5707963267948966F, 0.0F, 0.0F));

		root.addOrReplaceChild("leftBackLeg",
				CubeListBuilder.create()
						.texOffs(36, 32)
						.addBox(-1.5F, 0.0F, -1.5F, 3, 12, 3),
				PartPose.offsetAndRotation(3.0F, 12.0F, 9.5F, 0.0F, 0.0F, 0.0F));

		root.addOrReplaceChild("rightFrontLeg",
				CubeListBuilder.create()
						.texOffs(0, 32)
						.addBox(-1.5F, 0.0F, -1.5F, 3, 12, 3),
				PartPose.offsetAndRotation(-2.0F, 12.0F, 2.0F, 0.0F, 0.0F, 0.0F));

		head.addOrReplaceChild("rightEar",
				CubeListBuilder.create()
						.texOffs(0, 47)
						.addBox(-4.0F, -5.0F, -5.0F, 2, 2, 3),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

		body.addOrReplaceChild("tail",
				CubeListBuilder.create()
						.texOffs(36, 16)
						.addBox(-2.0F, -4.0F, 0.0F, 4, 5, 10),
				PartPose.offsetAndRotation(0.0F, 0.0F, 1.5F, -1.3089969389957472F, 0.0F, 0.0F));

		body.addOrReplaceChild("fluff",
				CubeListBuilder.create()
						.texOffs(28, 0)
						.addBox(-5.0F, 0.0F, -4.0F, 10, 8, 8),
				PartPose.offsetAndRotation(0.0F, -13.0F, 3.0F, 0.0F, 0.0F, 0.0F));

		root.addOrReplaceChild("leftFrontLeg",
				CubeListBuilder.create()
						.texOffs(12, 32)
						.addBox(-1.5F, 0.0F, -1.5F, 3, 12, 3),
				PartPose.offsetAndRotation(2.0F, 12.0F, 2.0F, 0.0F, 0.0F, 0.0F));

		root.addOrReplaceChild("rightBackLeg",
				CubeListBuilder.create()
						.texOffs(24, 32)
						.addBox(-1.5F, 0.0F, -1.5F, 3, 12, 3),
				PartPose.offsetAndRotation(-3.0F, 12.0F, 9.5F, 0.0F, 0.0F, 0.0F));

		head.addOrReplaceChild("leftEar",
				CubeListBuilder.create()
						.texOffs(10, 47)
						.addBox(2.0F, -5.0F, -5.0F, 2, 2, 3),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

		head.addOrReplaceChild("snout",
				CubeListBuilder.create()
						.texOffs(29, 18)
						.addBox(-2.0F, 1.0F, -10.0F, 4, 2, 4),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(mesh, 64, 64);
	}

	@Override
	public void prepareMobModel(Foxhound hound, float limbSwing, float limbSwingAmount, float partialTickTime) {
		this.entity = hound;
		if(hound.isOrderedToSit() || hound.getRemainingPersistentAngerTime() > 0)
			this.tail.xRot = -0.6544984695F;
		else
			this.tail.xRot = -1.3089969389957472F + Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;

		this.head.yRot = hound.getHeadRollAngle(partialTickTime) - hound.getBodyRollAngle(partialTickTime, 0.0F);
		this.head.xRot = 0;
		this.body.yRot = hound.getBodyRollAngle(partialTickTime, -0.16F);
		this.tail.yRot = hound.getBodyRollAngle(partialTickTime, -0.2F);

		if(hound.isSleeping()) {
			this.head.setPos(1.0F, 20.5F, 0.0F);
			this.setRotateAngle(head, 0.0F, 0.7853981633974483F, -0.04363323129985824F);

			this.body.setPos(0.0F, 20.0F, 12.0F);
			this.setRotateAngle(body, 1.5707963267948966F, 0.0F, 1.5707963267948966F);
			this.tail.setPos(0.0F, -1.0F, 1.0F);
			this.setRotateAngle(tail, 2.5497515042385164F, -0.22759093446006054F, 0.0F);
			this.rightFrontLeg.setPos(0.0F, 18.0F, 2.0F);
			this.leftFrontLeg.setPos(2.0F, 21.0F, 1.0F);
			this.rightBackLeg.setPos(0.0F, 22.0F, 11.0F);
			this.leftBackLeg.setPos(3.0F, 20.0F, 10.0F);

			this.setRotateAngle(rightFrontLeg, 0.2181661564992912F, 0.4363323129985824F, 1.3089969389957472F);
			this.setRotateAngle(leftFrontLeg, 0.0F, 0.0F, 1.3962634015954636F);
			this.setRotateAngle(rightBackLeg, -1.0471975511965976F, -0.08726646259971647F, 1.48352986419518F);
			this.setRotateAngle(leftBackLeg, -0.7853981633974483F, 0.0F, 1.2217304763960306F);
		} else if(hound.isInSittingPose()) {
			this.head.setPos(0.0F, 12.0F, 2.0F);
			this.body.setPos(0.0F, 23.0F, 7.0F);
			this.setRotateAngle(body, 0.7853981633974483F, this.body.yRot, 0F);
			this.tail.setPos(0.0F, 0.0F, -2.0F);
			this.setRotateAngle(tail, -0.5235987755982988F, -0.7243116395776468F, 0F);
			this.rightFrontLeg.setPos(-2.0F, 12.0F, 1.25F);
			this.leftFrontLeg.setPos(2.0F, 12.0F, 1.25F);
			this.rightBackLeg.setPos(-3.0F, 21.0F, 10.0F);
			this.leftBackLeg.setPos(3.0F, 21.0F, 10.0F);

			this.setRotateAngle(rightFrontLeg, 0F, 0F, 0F);
			this.setRotateAngle(leftFrontLeg, 0F, 0F, 0F);
			this.setRotateAngle(rightBackLeg, -1.3089969389957472F, 0.39269908169872414F, 0.0F);
			this.setRotateAngle(leftBackLeg, -1.3089969389957472F, -0.39269908169872414F, 0.0F);
		} else {
			this.head.setPos(0.0F, 14.5F, 0.0F);
			this.body.setPos(0.0F, 17.0F, 12.0F);
			this.setRotateAngle(body, 1.5707963267948966F, this.body.yRot, 0F);
			this.tail.setPos(0.0F, 0.0F, 1.5F);
			this.rightFrontLeg.setPos(-2.0F, 12.0F, 2.0F);
			this.leftFrontLeg.setPos(2.0F, 12.0F, 2.0F);
			this.rightBackLeg.setPos(-3.0F, 12.0F, 9.5F);
			this.leftBackLeg.setPos(3.0F, 12.0F, 9.5F);
			this.setRotateAngle(rightFrontLeg, Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount, 0, 0);
			this.setRotateAngle(leftFrontLeg, Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount, 0, 0);
			this.setRotateAngle(rightBackLeg, Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount, 0, 0);
			this.setRotateAngle(leftBackLeg, Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount, 0, 0);
		}
	}

	@Override
	public void setupAnim(Foxhound entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		if(!entity.isSleeping()) {
			head.yRot += netHeadYaw * 0.017453292F;
			head.xRot += headPitch * 0.017453292F;
		} else
			head.yRot += Mth.cos(entity.tickCount / 30f) / 20;
	}

	@Override
	public void renderToBuffer(PoseStack matrix, @NotNull VertexConsumer vb, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		matrix.pushPose();
		if(entity.isSleeping()) {
			matrix.mulPose(Axis.XP.rotationDegrees(90F));
			matrix.translate(0, -1.5, -1.5);
		}

		matrix.translate(0, 0, entity.isOrderedToSit() ? -0.25F : -0.35F);

		matrix.pushPose();

		if(young)
			matrix.translate(0.0F, 5.0F / 16F, 0F);

		head.render(matrix, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		matrix.popPose();

		matrix.pushPose();
		if(young) {
			matrix.translate(0.0F, 12.0F / 16F, 0F);
			matrix.scale(0.5F, 0.5F, 0.5F);
		}

		leftBackLeg.render(matrix, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightFrontLeg.render(matrix, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		body.render(matrix, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftFrontLeg.render(matrix, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightBackLeg.render(matrix, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		matrix.popPose();
		matrix.popPose();
	}

	public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}
