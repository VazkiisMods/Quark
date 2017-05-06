/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/06/2016, 00:14:07 (GMT)]
 */
package vazkii.quark.decoration.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.util.ModelHandler;
import vazkii.quark.decoration.entity.EntityColoredItemFrame;
import vazkii.quark.decoration.entity.EntityFlatItemFrame;

@SideOnly(Side.CLIENT)
public class RenderColoredItemFrame extends RenderFlatItemFrame {
	public static final IRenderFactory FACTORY = (RenderManager manager) -> new RenderColoredItemFrame(manager);

	public RenderColoredItemFrame(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected void renderModel(EntityFlatItemFrame entity, Minecraft mc) {
		EntityColoredItemFrame entityColored = (EntityColoredItemFrame) entity;
		BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
		ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
		IBakedModel ibakedmodel1, ibakedmodel2;

		if(!entity.getDisplayedItem().isEmpty() && entity.getDisplayedItem().getItem() == Items.FILLED_MAP) {
			ibakedmodel1 = modelmanager.getModel(ModelHandler.resourceLocations.get("colored_item_frame_map_wood"));
			ibakedmodel2 = modelmanager.getModel(ModelHandler.resourceLocations.get("colored_item_frame_map"));
		} else {
			ibakedmodel1 = modelmanager.getModel(ModelHandler.resourceLocations.get("colored_item_frame_wood"));
			ibakedmodel2 = modelmanager.getModel(ModelHandler.resourceLocations.get("colored_item_frame_normal"));
		}

		blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel1, 1.0F, 1.0F, 1.0F, 1.0F);

		int color = ItemDye.DYE_COLORS[15 - entityColored.getColor()];
		float r = (color >> 16 & 0xFF) / 255F;
		float g = (color >> 8 & 0xFF) / 255F;
		float b = (color & 0xFF) / 255F;

		blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel2, 1.0F, r, g, b);
	}
}