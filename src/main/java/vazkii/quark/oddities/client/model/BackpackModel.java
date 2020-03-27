package vazkii.quark.oddities.client.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.client.BaseArmorModel;

@OnlyIn(Dist.CLIENT)
public class BackpackModel extends BaseArmorModel {

	private final ModelRenderer straps;
	private final ModelRenderer backpack;
	private final ModelRenderer fitting;

	private final ModelRenderer base;

	public BackpackModel() {
		super(EquipmentSlotType.CHEST);
		base = new ModelRenderer(this);

		straps = new ModelRenderer(this, 24, 0);
		straps.setRotationPoint(0.0F, 0.0F, 0.0F);
		straps.addBox(-4.0F, 0.05F, -3.0F, 8, 8, 5, 0.0F);
		fitting = new ModelRenderer(this, 50, 0);
		fitting.setRotationPoint(0.0F, 0.0F, 0.0F);
		fitting.addBox(-1.0F, 3.0F, 6.0F, 2, 3, 1, 0.0F);
		backpack = new ModelRenderer(this, 0, 0);
		backpack.setRotationPoint(0.0F, 0.0F, 0.0F);
		backpack.addBox(-4.0F, 0.0F, 2.0F, 8, 10, 4, 0.0F);

		base.addChild(straps);
		base.addChild(backpack);
		base.addChild(fitting);
	}
	
	@Override
	public void setRotationAngles(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		bipedBody = base;
		super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
	}
	
}
