package org.violetmoon.quark.content.tweaks.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.tweaks.entity.DyedItemFrame;

// yes this shit again
@OnlyIn(Dist.CLIENT)
public class DyedItemFrameRenderer extends EntityRenderer<DyedItemFrame> {

	private static final ModelResourceLocation FRAME_LOCATION = new ModelResourceLocation(Quark.MOD_ID, "extra/dyed_item_frame", "inventory");
	private static final ModelResourceLocation MAP_FRAME_LOCATION = new ModelResourceLocation(Quark.MOD_ID, "extra/dyed_item_frame_map", "inventory");

	public static final int GLOW_FRAME_BRIGHTNESS = 5;
	public static final int BRIGHT_MAP_LIGHT_ADJUSTMENT = 30;
	private final ItemRenderer itemRenderer;
	private final BlockRenderDispatcher blockRenderer;

	public DyedItemFrameRenderer(EntityRendererProvider.Context p_174204_) {
		super(p_174204_);
		this.itemRenderer = p_174204_.getItemRenderer();
		this.blockRenderer = p_174204_.getBlockRenderDispatcher();
	}

	@Override
	protected int getBlockLightLevel(DyedItemFrame p_174216_, BlockPos p_174217_) {
		return p_174216_.isGlow() ? Math.max(5, super.getBlockLightLevel(p_174216_, p_174217_)) : super.getBlockLightLevel(p_174216_, p_174217_);
	}

	@Override
	public void render(DyedItemFrame p_115076_, float p_115077_, float p_115078_, PoseStack p_115079_, MultiBufferSource p_115080_, int p_115081_) {
		super.render(p_115076_, p_115077_, p_115078_, p_115079_, p_115080_, p_115081_);
		p_115079_.pushPose();
		Direction direction = p_115076_.getDirection();
		Vec3 vec3 = this.getRenderOffset(p_115076_, p_115078_);
		p_115079_.translate(-vec3.x(), -vec3.y(), -vec3.z());
		p_115079_.translate((double)direction.getStepX() * 0.46875D, (double)direction.getStepY() * 0.46875D, (double)direction.getStepZ() * 0.46875D);
		p_115079_.mulPose(Vector3f.XP.rotationDegrees(p_115076_.getXRot()));
		p_115079_.mulPose(Vector3f.YP.rotationDegrees(180.0F - p_115076_.getYRot()));
		boolean flag = p_115076_.isInvisible();
		ItemStack itemstack = p_115076_.getItem();
		if (!flag) {
			ModelManager modelmanager = this.blockRenderer.getBlockModelShaper().getModelManager();
			boolean map = p_115076_.getItem().getItem() instanceof MapItem;
			ModelResourceLocation modelresourcelocation = map ? MAP_FRAME_LOCATION : FRAME_LOCATION;
			
			p_115079_.pushPose();
			p_115079_.translate(-0.5D, -0.5D, -0.5D);
			
			int color = p_115076_.getColor();
			float r = ((float) ((color >> 16) & 0xFF)) / 255F;
			float g = ((float) ((color >>  8) & 0xFF)) / 255F;
			float b = ((float) ((color      ) & 0xFF)) / 255F;
			
			blockRenderer.getModelRenderer().renderModel(p_115079_.last(), p_115080_.getBuffer(Sheets.solidBlockSheet()), (BlockState)null, modelmanager.getModel(modelresourcelocation), r, g, b, p_115081_, OverlayTexture.NO_OVERLAY);
			p_115079_.popPose();
		}

		if (!itemstack.isEmpty()) {
			MapItemSavedData mapitemsaveddata = MapItem.getSavedData(itemstack, p_115076_.level);
			if (flag) {
				p_115079_.translate(0.0D, 0.0D, 0.5D);
			} else {
				p_115079_.translate(0.0D, 0.0D, 0.4375D);
			}

			int j = mapitemsaveddata != null ? p_115076_.getRotation() % 4 * 2 : p_115076_.getRotation();
			p_115079_.mulPose(Vector3f.ZP.rotationDegrees((float)j * 360.0F / 8.0F));
			if (mapitemsaveddata != null) {
				p_115079_.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
				p_115079_.scale(0.0078125F, 0.0078125F, 0.0078125F);
				p_115079_.translate(-64.0D, -64.0D, 0.0D);
				p_115079_.translate(0.0D, 0.0D, -1.0D);
				if (mapitemsaveddata != null) {
					int i = this.getLightVal(p_115076_, 15728850, p_115081_);
					Minecraft.getInstance().gameRenderer.getMapRenderer().render(p_115079_, p_115080_, p_115076_.getFramedMapId().getAsInt(), mapitemsaveddata, true, i);
				}
			} else {
				int k = this.getLightVal(p_115076_, 15728880, p_115081_);
				p_115079_.scale(0.5F, 0.5F, 0.5F);
				this.itemRenderer.renderStatic(itemstack, ItemTransforms.TransformType.FIXED, k, OverlayTexture.NO_OVERLAY, p_115079_, p_115080_, p_115076_.getId());
			}
		}

		p_115079_.popPose();
	}

	private int getLightVal(DyedItemFrame p_174209_, int p_174210_, int p_174211_) {
		return p_174209_.isGlow() ? p_174210_ : p_174211_;
	}

	@Override
	public Vec3 getRenderOffset(DyedItemFrame p_115073_, float p_115074_) {
		return new Vec3((double)((float)p_115073_.getDirection().getStepX() * 0.3F), -0.25D, (double)((float)p_115073_.getDirection().getStepZ() * 0.3F));
	}

	@Override
	public ResourceLocation getTextureLocation(DyedItemFrame p_115071_) {
		return TextureAtlas.LOCATION_BLOCKS;
	}

	@Override
	protected boolean shouldShowName(DyedItemFrame p_115091_) {
		if (Minecraft.renderNames() && !p_115091_.getItem().isEmpty() && p_115091_.getItem().hasCustomHoverName() && this.entityRenderDispatcher.crosshairPickEntity == p_115091_) {
			double d0 = this.entityRenderDispatcher.distanceToSqr(p_115091_);
			float f = p_115091_.isDiscrete() ? 32.0F : 64.0F;
			return d0 < (double)(f * f);
		} else {
			return false;
		}
	}

	@Override
	protected void renderNameTag(DyedItemFrame p_115083_, Component p_115084_, PoseStack p_115085_, MultiBufferSource p_115086_, int p_115087_) {
		super.renderNameTag(p_115083_, p_115083_.getItem().getHoverName(), p_115085_, p_115086_, p_115087_);
	}
}
