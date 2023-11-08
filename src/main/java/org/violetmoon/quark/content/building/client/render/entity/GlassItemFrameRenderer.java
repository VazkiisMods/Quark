package org.violetmoon.quark.content.building.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.common.MinecraftForge;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.building.entity.GlassItemFrame;
import org.violetmoon.quark.content.building.module.GlassItemFrameModule;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author WireSegal
 * Created at 11:58 AM on 8/25/19.
 */

@OnlyIn(Dist.CLIENT)
public class GlassItemFrameRenderer extends EntityRenderer<GlassItemFrame> {

	private static final ModelResourceLocation LOCATION_MODEL = new ModelResourceLocation(new ResourceLocation(Quark.MOD_ID, "extra/glass_item_frame"), "inventory");

	private static final List<Direction> SIGN_DIRECTIONS = List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

	private static final BannerBlockEntity banner = new BannerBlockEntity(BlockPos.ZERO, Blocks.WHITE_BANNER.defaultBlockState());
	private final ModelPart bannerModel;

	private final Minecraft mc = Minecraft.getInstance();
	private final ItemRenderer itemRenderer;
	private final ItemFrameRenderer<?> defaultRenderer;

	public GlassItemFrameRenderer(EntityRendererProvider.Context context) {
		super(context);

		ModelPart part = context.bakeLayer(ModelLayers.BANNER);
		this.bannerModel = part.getChild("flag");

		Minecraft mc = Minecraft.getInstance();
		this.itemRenderer = mc.getItemRenderer();
		this.defaultRenderer = (ItemFrameRenderer<?>) mc.getEntityRenderDispatcher().renderers.get(EntityType.ITEM_FRAME);
	}

	@Override
	public void render(@Nonnull GlassItemFrame frame, float yaw, float partialTicks, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource buffer, int light) {
		super.render(frame, yaw, partialTicks, matrix, buffer, light);
		matrix.pushPose();
		Direction direction = frame.getDirection();
		Vec3 Vector3d = this.getRenderOffset(frame, partialTicks);
		matrix.translate(-Vector3d.x(), -Vector3d.y(), -Vector3d.z());
		matrix.translate((double)direction.getStepX() * 0.46875D, (double)direction.getStepY() * 0.46875D, (double)direction.getStepZ() * 0.46875D);
		matrix.mulPose(Vector3f.XP.rotationDegrees(frame.getXRot()));
		matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F - frame.getYRot()));
		BlockRenderDispatcher blockrendererdispatcher = this.mc.getBlockRenderer();
		ModelManager modelmanager = blockrendererdispatcher.getBlockModelShaper().getModelManager();

		ItemStack itemstack = frame.getItem();

		if(frame.getEntityData().get(GlassItemFrame.IS_SHINY))
			light = 0xF000F0;

		if (itemstack.isEmpty()) {
			matrix.pushPose();
			matrix.translate(-0.5D, -0.5D, -0.5D);
			blockrendererdispatcher.getModelRenderer().renderModel(matrix.last(), buffer.getBuffer(Sheets.cutoutBlockSheet()), null, modelmanager.getModel(LOCATION_MODEL), 1.0F, 1.0F, 1.0F, light, OverlayTexture.NO_OVERLAY);
			matrix.popPose();
		} else {
			renderItemStack(frame, matrix, buffer, light, itemstack);
		}

		matrix.popPose();
	}

	@Nonnull
	@Override
	public Vec3 getRenderOffset(GlassItemFrame frame, float partialTicks) {
		return new Vec3((float)frame.getDirection().getStepX() * 0.3F, -0.25D, (float)frame.getDirection().getStepZ() * 0.3F);
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull GlassItemFrame frame) {
		return TextureAtlas.LOCATION_BLOCKS;
	}

	@Override
	protected boolean shouldShowName(@Nonnull GlassItemFrame frame) {
		if (Minecraft.renderNames() && !frame.getItem().isEmpty() && frame.getItem().hasCustomHoverName() && this.entityRenderDispatcher.crosshairPickEntity == frame) {
			double d0 = this.entityRenderDispatcher.distanceToSqr(frame);
			float f = frame.isDiscrete() ? 32.0F : 64.0F;
			return d0 < (double)(f * f);
		} else {
			return false;
		}
	}

	@Override
	protected void renderNameTag(@Nonnull GlassItemFrame frame, @Nonnull Component text, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource buffer, int light) {
		super.renderNameTag(frame, frame.getItem().getHoverName(), matrix, buffer, light);
	}

	protected void renderItemStack(GlassItemFrame itemFrame, PoseStack matrix, MultiBufferSource buff, int light, ItemStack stack) {
		if (!stack.isEmpty()) {
			matrix.pushPose();
			MapItemSavedData mapdata = MapItem.getSavedData(stack, itemFrame.level);

			sign: if(itemFrame.isOnSign()) {

				Direction ourDirection = itemFrame.getDirection().getOpposite();

				int signRotation = itemFrame.getOnSignRotation();
				Direction signDirection = SIGN_DIRECTIONS.get(signRotation / 4);
				if(signRotation % 4 == 0 ? (signDirection != ourDirection) : (signDirection.getOpposite() == ourDirection))
					break sign;

				int ourRotation = SIGN_DIRECTIONS.indexOf(ourDirection) * 4;
				int rotation = signRotation - ourRotation;
				float angle = -rotation * 22.5F;

				matrix.translate(0, 0.35, 0.8);
				matrix.scale(0.4F, 0.4F, 0.4F);
				matrix.translate(0, 0, 0.5);
				matrix.mulPose(Vector3f.YP.rotationDegrees(angle));
				matrix.translate(0, 0, -0.5);
				matrix.translate(0, 0, -0.085);
			}

			int rotation = mapdata != null ? itemFrame.getRotation() % 4 * 2 : itemFrame.getRotation();
			matrix.mulPose(Vector3f.ZP.rotationDegrees((float) rotation * 360.0F / 8.0F));

			if (!MinecraftForge.EVENT_BUS.post(new RenderItemInFrameEvent(itemFrame, defaultRenderer, matrix, buff, light))) {
				if (mapdata != null) {
					matrix.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
					matrix.scale(0.0078125F, 0.0078125F, 0.0078125F);
					matrix.translate(-64.0F, -64.0F, 62.5F); // <- Use 62.5 instead of 64 to prevent z-fighting

					Integer mapID = MapItem.getMapId(stack);
					this.mc.gameRenderer.getMapRenderer().render(matrix, buff, mapID, mapdata, true, light);
				} else {
					float s = (float) GlassItemFrameModule.itemRenderScale;
					if (stack.getItem() instanceof BannerItem bannerItem) {
						banner.fromItem(stack, bannerItem.getColor());
						List<Pair<Holder<BannerPattern>, DyeColor>> patterns = banner.getPatterns();

						matrix.pushPose();
						matrix.translate(0.0001F, -0.5001F, 0.55F);
						matrix.scale(0.799999F, 0.399999F, 0.5F);
						BannerRenderer.renderPatterns(matrix, buff, light, OverlayTexture.NO_OVERLAY, bannerModel, ModelBakery.BANNER_BASE, true, patterns);
						matrix.popPose();
					}
					else {
						if (stack.getItem() instanceof ShieldItem) {
							s *= 2.66666667F;
							matrix.translate(-0.25F, 0F, 0.5F);
							matrix.scale(s, s, s);
						} else {
							matrix.translate(0F, 0F, 0.475F);
							matrix.scale(s, s, s);
						}
						matrix.scale(0.5F, 0.5F, 0.5F);
						this.itemRenderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrix, buff, 0);
					}
				}
			}

			matrix.popPose();
		}
	}
}
