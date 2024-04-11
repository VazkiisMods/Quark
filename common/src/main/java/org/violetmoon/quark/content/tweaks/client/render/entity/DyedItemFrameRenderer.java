package org.violetmoon.quark.content.tweaks.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.tweaks.entity.DyedItemFrame;

// yes this shit again
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
	public void render(@NotNull DyedItemFrame dyedItemFrame, float p_115077_, float p_115078_, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int p_115081_) {
		super.render(dyedItemFrame, p_115077_, p_115078_, poseStack, multiBufferSource, p_115081_);
		poseStack.pushPose();
		Direction direction = dyedItemFrame.getDirection();
		Vec3 vec3 = this.getRenderOffset(dyedItemFrame, p_115078_);
		poseStack.translate(-vec3.x(), -vec3.y(), -vec3.z());
		poseStack.translate((double) direction.getStepX() * 0.46875D, (double) direction.getStepY() * 0.46875D, (double) direction.getStepZ() * 0.46875D);
		poseStack.mulPose(Axis.XP.rotationDegrees(dyedItemFrame.getXRot()));
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - dyedItemFrame.getYRot()));
		boolean flag = dyedItemFrame.isInvisible();
		ItemStack itemstack = dyedItemFrame.getItem();
		if(!flag) {
			ModelManager modelmanager = this.blockRenderer.getBlockModelShaper().getModelManager();
			boolean map = dyedItemFrame.getItem().getItem() instanceof MapItem;
			ModelResourceLocation modelresourcelocation = map ? MAP_FRAME_LOCATION : FRAME_LOCATION;

			poseStack.pushPose();
			poseStack.translate(-0.5D, -0.5D, -0.5D);

			int color = dyedItemFrame.getColor();
			float r = ((float) ((color >> 16) & 0xFF)) / 255F;
			float g = ((float) ((color >> 8) & 0xFF)) / 255F;
			float b = ((float) ((color) & 0xFF)) / 255F;

			blockRenderer.getModelRenderer().renderModel(poseStack.last(), multiBufferSource.getBuffer(Sheets.solidBlockSheet()), (BlockState) null, modelmanager.getModel(modelresourcelocation), r, g, b, p_115081_, OverlayTexture.NO_OVERLAY);
			poseStack.popPose();
		}

		if(!itemstack.isEmpty()) {
			MapItemSavedData mapitemsaveddata = MapItem.getSavedData(itemstack, dyedItemFrame.level());
			if(flag) {
				poseStack.translate(0.0D, 0.0D, 0.5D);
			} else {
				poseStack.translate(0.0D, 0.0D, 0.4375D);
			}

			int j = mapitemsaveddata != null ? dyedItemFrame.getRotation() % 4 * 2 : dyedItemFrame.getRotation();
			poseStack.mulPose(Axis.ZP.rotationDegrees((float) j * 360.0F / 8.0F));
			if(mapitemsaveddata != null) {
				poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
				poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
				poseStack.translate(-64.0D, -64.0D, 0.0D);
				poseStack.translate(0.0D, 0.0D, -1.0D);
				if(mapitemsaveddata != null) {
					int i = this.getLightVal(dyedItemFrame, 15728850, p_115081_);
					Minecraft.getInstance().gameRenderer.getMapRenderer().render(poseStack, multiBufferSource, dyedItemFrame.getFramedMapId().getAsInt(), mapitemsaveddata, true, i);
				}
			} else {
				int k = this.getLightVal(dyedItemFrame, 15728880, p_115081_);
				poseStack.scale(0.5F, 0.5F, 0.5F);
				this.itemRenderer.renderStatic(itemstack, ItemDisplayContext.FIXED, k, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, Minecraft.getInstance().level, dyedItemFrame.getId());
			}
		}

		poseStack.popPose();
	}

	private int getLightVal(DyedItemFrame p_174209_, int p_174210_, int p_174211_) {
		return p_174209_.isGlow() ? p_174210_ : p_174211_;
	}

	@Override
	public Vec3 getRenderOffset(DyedItemFrame p_115073_, float p_115074_) {
		return new Vec3((float) p_115073_.getDirection().getStepX() * 0.3F, -0.25D, (float) p_115073_.getDirection().getStepZ() * 0.3F);
	}

	@Override
	public ResourceLocation getTextureLocation(DyedItemFrame p_115071_) {
		return InventoryMenu.BLOCK_ATLAS;
	}

	@Override
	protected boolean shouldShowName(DyedItemFrame p_115091_) {
		if(Minecraft.renderNames() && !p_115091_.getItem().isEmpty() && p_115091_.getItem().hasCustomHoverName() && this.entityRenderDispatcher.crosshairPickEntity == p_115091_) {
			double d0 = this.entityRenderDispatcher.distanceToSqr(p_115091_);
			float f = p_115091_.isDiscrete() ? 32.0F : 64.0F;
			return d0 < (double) (f * f);
		} else {
			return false;
		}
	}

	@Override
	protected void renderNameTag(DyedItemFrame p_115083_, Component p_115084_, PoseStack p_115085_, MultiBufferSource p_115086_, int p_115087_) {
		super.renderNameTag(p_115083_, p_115083_.getItem().getHoverName(), p_115085_, p_115086_, p_115087_);
	}
}
