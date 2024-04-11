package org.violetmoon.quark.addons.oddities.client.model;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import org.violetmoon.quark.base.client.render.QuarkArmorModel;

public class BackpackModel {

	public static LayerDefinition createBodyLayer() {
		return QuarkArmorModel.createLayer(64, 32, root -> {
			PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);

			body.addOrReplaceChild("straps",
					CubeListBuilder.create()
							.texOffs(24, 0)
							.addBox(-4.0F, 0.05F, -3.0F, 8, 8, 5),
					PartPose.ZERO);

			body.addOrReplaceChild("fitting",
					CubeListBuilder.create()
							.texOffs(50, 0)
							.addBox(-1.0F, 3.0F, 6.0F, 2, 3, 1),
					PartPose.ZERO);

			body.addOrReplaceChild("backpack",
					CubeListBuilder.create()
							.texOffs(0, 0)
							.addBox(-4.0F, 0.0F, 2.0F, 8, 10, 4),
					PartPose.ZERO);
		});
	}

}
