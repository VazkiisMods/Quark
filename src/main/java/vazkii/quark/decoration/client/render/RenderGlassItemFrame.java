package vazkii.quark.decoration.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.arl.util.ModelHandler;
import vazkii.quark.decoration.entity.EntityFlatItemFrame;

public class RenderGlassItemFrame extends RenderFlatItemFrame {

	public static final IRenderFactory FACTORY = (RenderManager manager) -> new RenderGlassItemFrame(manager);
	
	public RenderGlassItemFrame(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}
	
	@Override
	protected void renderModel(EntityFlatItemFrame entity, Minecraft mc) {
		BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
		ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();

		if(entity.getDisplayedItem().isEmpty()) {
			IBakedModel ibakedmodel = modelmanager.getModel(ModelHandler.resourceLocations.get("glass_item_frame_world"));
			blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
	
	@Override
	protected void transformItem(EntityItemFrame frame, ItemStack stack) {
		float s = 1.5F;
		if(stack.getItem() instanceof ItemShield) {
			s = 4F;
			GlStateManager.translate(-0.25F, 0F, 0.2F);
			GlStateManager.scale(s, s, s);
		} else {
			GlStateManager.translate(0F, 0F, 0.05F);
			GlStateManager.scale(s, s, s);
		}
		
		super.transformItem(frame, stack);
	}

}
