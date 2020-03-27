package vazkii.quark.oddities.client.render;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import vazkii.quark.automation.client.render.PistonTileEntityRenderer;
import vazkii.quark.oddities.tile.MagnetizedBlockTileEntity;

@OnlyIn(Dist.CLIENT)
public class MagnetizedBlockTileEntityRenderer extends TileEntityRenderer<MagnetizedBlockTileEntity> {

	private BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
	
	public MagnetizedBlockTileEntityRenderer(TileEntityRendererDispatcher d) {
		super(d);
	}

	@SuppressWarnings("deprecation")
	public void render(MagnetizedBlockTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		World world = tileEntityIn.getWorld();
		if (world != null) {
			BlockPos truepos = tileEntityIn.getPos();
			BlockPos blockpos = truepos.offset(tileEntityIn.getFacing().getOpposite());
			BlockState blockstate = tileEntityIn.getMagnetState();
			if (!blockstate.isAir() && !(tileEntityIn.getProgress(partialTicks) >= 1.0F)) {
				TileEntity subTile = tileEntityIn.getSubTile();
				Vec3d offset = new Vec3d(tileEntityIn.getOffsetX(partialTicks), tileEntityIn.getOffsetY(partialTicks), tileEntityIn.getOffsetZ(partialTicks));
				if(PistonTileEntityRenderer.renderTESafely(world, truepos, blockstate, subTile, tileEntityIn, partialTicks, offset, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn))
					return;
				
				BlockModelRenderer.enableCache();
				matrixStackIn.push();
				matrixStackIn.translate(offset.x, offset.y, offset.z);
				if (blockstate.getBlock() == Blocks.PISTON_HEAD && tileEntityIn.getProgress(partialTicks) <= 4.0F) {
					blockstate = blockstate.with(PistonHeadBlock.SHORT, Boolean.valueOf(true));
					renderStateModel(blockpos, blockstate, matrixStackIn, bufferIn, world, false, combinedOverlayIn);
				} else {
					renderStateModel(blockpos, blockstate, matrixStackIn, bufferIn, world, false, combinedOverlayIn);
				}

				matrixStackIn.pop();
				BlockModelRenderer.disableCache();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void renderStateModel(BlockPos p_228876_1_, BlockState p_228876_2_, MatrixStack p_228876_3_, IRenderTypeBuffer p_228876_4_, World p_228876_5_, boolean p_228876_6_, int p_228876_7_) {
		RenderType.getBlockRenderTypes().stream().filter(t -> RenderTypeLookup.canRenderInLayer(p_228876_2_, t)).forEach(rendertype -> {
			ForgeHooksClient.setRenderLayer(rendertype);
			IVertexBuilder ivertexbuilder = p_228876_4_.getBuffer(rendertype);
			if (blockRenderer == null) 
				blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
			
			blockRenderer.getBlockModelRenderer().renderModel(p_228876_5_, blockRenderer.getModelForState(p_228876_2_), p_228876_2_, p_228876_1_, p_228876_3_, ivertexbuilder, p_228876_6_, new Random(), p_228876_2_.getPositionRandom(p_228876_1_), p_228876_7_);
		});
		ForgeHooksClient.setRenderLayer(null);
	}
}
