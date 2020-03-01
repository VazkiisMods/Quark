package vazkii.quark.automation.client.render;

import java.util.Objects;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.PistonTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import vazkii.quark.automation.module.PistonsMoveTileEntitiesModule;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.ModuleLoader;

public class PistonTileEntityRenderer {

	public static boolean renderPistonBlock(PistonTileEntity piston, double x, double y, double z, float pTicks) {
		if (!ModuleLoader.INSTANCE.isModuleEnabled(PistonsMoveTileEntitiesModule.class) || piston.getProgress(pTicks) > 1.0F)
			return false;

		BlockState state = piston.getPistonState();
		Block block = state.getBlock();
		String id = Objects.toString(block.getRegistryName());
		BlockPos truePos = piston.getPos();

		try {
			TileEntity tile = PistonsMoveTileEntitiesModule.getMovement(piston.getWorld(), truePos);
			
			if(tile == null || PistonsMoveTileEntitiesModule.renderBlacklist.contains(id))
				return false;

			RenderSystem.pushMatrix();
			tile.setWorldAndPos(piston.getWorld(), piston.getPos());
			tile.validate();

			RenderSystem.translated(x + piston.getOffsetX(pTicks), y + piston.getOffsetY(pTicks), z + piston.getOffsetZ(pTicks));

			RenderHelper.enableStandardItemLighting();
			tile.cachedBlockState = state;
			TileEntityRenderer<TileEntity> tileentityrenderer = TileEntityRendererDispatcher.instance.getRenderer(tile);
			// TODO: WIRE: update the passed values
			// we need the params for the render method
			
//			if (tileentityrenderer != null)
//				tileentityrenderer.render(tile, 0, 0, 0, pTicks, -1);
			RenderHelper.disableStandardItemLighting();
			RenderSystem.popMatrix();

		} catch(Throwable e) {
			Quark.LOG.warn(id + " can't be rendered for piston TE moving", e);
			PistonsMoveTileEntitiesModule.renderBlacklist.add(id);
			return false;
		}

		return state.getRenderType() != BlockRenderType.MODEL;
	}

}
