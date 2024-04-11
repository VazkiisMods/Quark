package org.violetmoon.quark.content.mobs.client.model;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import org.violetmoon.quark.base.client.render.QuarkArmorModel;

public class ForgottenHatModel {

	public static LayerDefinition createBodyLayer() {
		return QuarkArmorModel.createLayer(64, 64, root -> {
			PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);

			head.addOrReplaceChild("piece1",
					CubeListBuilder.create()
							.texOffs(0, 0)
							.addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, new CubeDeformation(0.6F)),
					PartPose.ZERO);

			head.addOrReplaceChild("piece2",
					CubeListBuilder.create()
							.texOffs(0, 18)
							.addBox(-6.0F, -6.0F, -6.0F, 12, 1, 12),
					PartPose.ZERO);
		});
	}

}
