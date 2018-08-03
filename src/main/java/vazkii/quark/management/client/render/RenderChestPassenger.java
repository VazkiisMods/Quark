package vazkii.quark.management.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.management.entity.EntityChestPassenger;

public class RenderChestPassenger extends Render<EntityChestPassenger> {

	protected RenderChestPassenger(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityChestPassenger entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		if(!entity.isRiding())
			return;

		Entity boat = entity.getRidingEntity();
		float rot = 180F - entityYaw;
		
		ItemStack stack = entity.getChestType();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(rot, 0F, 1F, 0F);
		GlStateManager.translate(0F, 0.7F, -0.15F);
		if(boat.getPassengers().size() == 1)
			GlStateManager.translate(0F, 0F, 0.6F);	
		
		GlStateManager.scale(1.75F, 1.75F, 1.75F);
		
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);		
		GlStateManager.popMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityChestPassenger entity) {
		return null;
	}
	
	public static IRenderFactory factory() {
		return manager -> new RenderChestPassenger(manager);
	}


}
