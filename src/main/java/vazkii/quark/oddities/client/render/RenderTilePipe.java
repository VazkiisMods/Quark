package vazkii.quark.oddities.client.render;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import vazkii.quark.oddities.tile.TilePipe;
import vazkii.quark.oddities.tile.TilePipe.PipeItem;

public class RenderTilePipe extends TileEntitySpecialRenderer<TilePipe> {

	@Override
	public void render(TilePipe te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
		RenderItem render = Minecraft.getMinecraft().getRenderItem();
		Iterator<PipeItem> items = te.getItemIterator();

		while(items.hasNext())
			renderItem(items.next(), render, partialTicks);
		GlStateManager.popMatrix();
	}

	private void renderItem(PipeItem item, RenderItem render, float pticks) { 
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		RenderHelper.enableStandardItemLighting();

		float scale = 0.4F;
		float fract = item.getTimeFract(pticks);
		float afract = fract - 0.5F;
		EnumFacing face = item.outgoingFace;
		if(fract < 0.5)
			face = item.incomingFace.getOpposite();
		
		float offX = (face.getFrontOffsetX() * 1F);
		float offY = (face.getFrontOffsetY() * 1F);
		float offZ = (face.getFrontOffsetZ() * 1F);
		GlStateManager.translate(offX * afract, offY * afract, offZ * afract);

		GlStateManager.scale(scale, scale, scale);
		
		float speed = 4F;
		GlStateManager.rotate((item.timeInWorld + pticks) * speed, 0F, 1F, 0F);
		
		render.renderItem(item.stack, ItemCameraTransforms.TransformType.FIXED);

		RenderHelper.disableStandardItemLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

}
