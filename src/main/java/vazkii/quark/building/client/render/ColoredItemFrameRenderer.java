package vazkii.quark.building.client.render;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.EntityType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.Quark;
import vazkii.quark.building.entity.ColoredItemFrameEntity;

/**
 * @author WireSegal
 * Created at 11:58 AM on 8/25/19.
 */
@OnlyIn(Dist.CLIENT)
public class ColoredItemFrameRenderer extends EntityRenderer<ColoredItemFrameEntity> {

	private static final ResourceLocation MAP_BACKGROUND_TEXTURES = new ResourceLocation("textures/map/map_background.png");

	private static final Map<DyeColor, ModelResourceLocation> LOCATIONS_MODEL = new HashMap<>();
	private static final Map<DyeColor, ModelResourceLocation> LOCATIONS_MODEL_MAP = new HashMap<>();

	private final Minecraft mc = Minecraft.getInstance();
	private final ItemRenderer itemRenderer;
	private final ItemFrameRenderer defaultRenderer;

	public ColoredItemFrameRenderer(EntityRendererManager renderManagerIn, ItemRenderer itemRendererIn) {
		super(renderManagerIn);
		this.itemRenderer = itemRendererIn;
		this.defaultRenderer = (ItemFrameRenderer) renderManagerIn.renderers.get(EntityType.ITEM_FRAME);

		for (DyeColor color : DyeColor.values()) {
			// reinstate when Forge fixes itself
			//            LOCATIONS_MODEL.put(color, new ModelResourceLocation(new ResourceLocation(Quark.MOD_ID, color.getName() + "_frame"), "map=false"));
			//            LOCATIONS_MODEL_MAP.put(color, new ModelResourceLocation(new ResourceLocation(Quark.MOD_ID, color.getName() + "_frame"), "map=true"));

			LOCATIONS_MODEL.put(color, new ModelResourceLocation(new ResourceLocation(Quark.MOD_ID, color.getName() + "_frame_empty"), "inventory"));
			LOCATIONS_MODEL_MAP.put(color, new ModelResourceLocation(new ResourceLocation(Quark.MOD_ID, color.getName() + "_frame_map"), "inventory"));
		}
	}

	@Override
	public void render(ColoredItemFrameEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
		super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
		p_225623_4_.push();
		Direction direction = p_225623_1_.getHorizontalFacing();
		Vec3d vec3d = this.getRenderOffset(p_225623_1_, p_225623_3_);
		p_225623_4_.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
		p_225623_4_.translate((double)direction.getXOffset() * 0.46875D, (double)direction.getYOffset() * 0.46875D, (double)direction.getZOffset() * 0.46875D);
		p_225623_4_.rotate(Vector3f.XP.rotationDegrees(p_225623_1_.rotationPitch));
		p_225623_4_.rotate(Vector3f.YP.rotationDegrees(180.0F - p_225623_1_.rotationYaw));
		BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
		ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
		DyeColor color = p_225623_1_.getColor();
		ModelResourceLocation modelresourcelocation = p_225623_1_.getDisplayedItem().getItem() instanceof FilledMapItem ? LOCATIONS_MODEL_MAP.get(color) : LOCATIONS_MODEL.get(color);
		p_225623_4_.push();
		p_225623_4_.translate(-0.5D, -0.5D, -0.5D);
		blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(p_225623_4_.getLast(), p_225623_5_.getBuffer(Atlases.getSolidBlockType()), (BlockState)null, modelmanager.getModel(modelresourcelocation), 1.0F, 1.0F, 1.0F, p_225623_6_, OverlayTexture.NO_OVERLAY);
		p_225623_4_.pop();
		ItemStack itemstack = p_225623_1_.getDisplayedItem();
		if (!itemstack.isEmpty()) {
			MapData mapdata = FilledMapItem.getMapData(itemstack, p_225623_1_.world);
			p_225623_4_.translate(0.0D, 0.0D, 0.4375D);
			int i = mapdata != null ? p_225623_1_.getRotation() % 4 * 2 : p_225623_1_.getRotation();
			p_225623_4_.rotate(Vector3f.ZP.rotationDegrees((float)i * 360.0F / 8.0F));
			if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderItemInFrameEvent(p_225623_1_, defaultRenderer, p_225623_4_, p_225623_5_, p_225623_6_))) {
				if (mapdata != null) {
					p_225623_4_.rotate(Vector3f.ZP.rotationDegrees(180.0F));
					float f = 0.0078125F;
					p_225623_4_.scale(0.0078125F, 0.0078125F, 0.0078125F);
					p_225623_4_.translate(-64.0D, -64.0D, 0.0D);
					p_225623_4_.translate(0.0D, 0.0D, -1.0D);
					if (mapdata != null) {
						this.mc.gameRenderer.getMapItemRenderer().renderMap(p_225623_4_, p_225623_5_, mapdata, true, p_225623_6_);
					}
				} else {
					p_225623_4_.scale(0.5F, 0.5F, 0.5F);
					this.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED, p_225623_6_, OverlayTexture.NO_OVERLAY, p_225623_4_, p_225623_5_);
				}
			}
		}

		p_225623_4_.pop();
	}

	@Override
	public Vec3d getRenderOffset(ColoredItemFrameEntity p_225627_1_, float p_225627_2_) {
		return new Vec3d((double)((float)p_225627_1_.getHorizontalFacing().getXOffset() * 0.3F), -0.25D, (double)((float)p_225627_1_.getHorizontalFacing().getZOffset() * 0.3F));
	}

	@Override
	public ResourceLocation getEntityTexture(ColoredItemFrameEntity p_110775_1_) {
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}

	@Override
	protected boolean canRenderName(ColoredItemFrameEntity p_177070_1_) {
		if (Minecraft.isGuiEnabled() && !p_177070_1_.getDisplayedItem().isEmpty() && p_177070_1_.getDisplayedItem().hasDisplayName() && this.renderManager.pointedEntity == p_177070_1_) {
			double d0 = this.renderManager.squareDistanceTo(p_177070_1_);
			float f = p_177070_1_.isDiscrete() ? 32.0F : 64.0F;
			return d0 < (double)(f * f);
		} else {
			return false;
		}
	}

	@Override
	protected void renderName(ColoredItemFrameEntity p_225629_1_, String p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
		super.renderName(p_225629_1_, p_225629_1_.getDisplayedItem().getDisplayName().getFormattedText(), p_225629_3_, p_225629_4_, p_225629_5_);
	}
}
