package vazkii.quark.content.tools.module;

import java.util.List;
import java.util.function.Consumer;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.inputtable.RGBAColorConfig;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.tools.item.AbacusItem;

@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class AbacusModule extends QuarkModule {

	@Hint public static Item abacus;
	@Config RGBAColorConfig highlightColor = RGBAColorConfig.forColor(0, 0, 0, 0.4);

	@Override
	public void register() {
		abacus = new AbacusItem(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup(Consumer<Runnable> enqueueWork) {
		enqueueWork.accept(() -> ItemProperties.register(abacus, new ResourceLocation("count"), AbacusItem::count));
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onHUDRender(RenderGuiOverlayEvent event) {
		if(event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type()) {
			Minecraft mc = Minecraft.getInstance();
			Player player = mc.player;
			if(player != null) {
				ItemStack stack = player.getMainHandItem();
				if(!(stack.getItem() instanceof AbacusItem))
					stack = player.getOffhandItem();

				if(stack.getItem() instanceof AbacusItem) {
					int distance = AbacusItem.getCount(stack, player);
					if(distance > -1) {
						Window window = event.getWindow();
						int x = window.getGuiScaledWidth() / 2 + 10;
						int y = window.getGuiScaledHeight() / 2 - 7;

						mc.getItemRenderer().renderAndDecorateItem(stack, x, y);

						String distStr = distance < AbacusItem.MAX_COUNT ? Integer.toString(distance + 1) : (AbacusItem.MAX_COUNT + "+");
						mc.font.drawShadow(event.getPoseStack(), distStr, x + 17, y + 5, 0xFFFFFF);
					}
				}
			}
		}
	}
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onHighlightBlock(RenderHighlightEvent.Block event) {
		VertexConsumer bufferIn = event.getMultiBufferSource().getBuffer(RenderType.lines());

		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if(player != null) {
			ItemStack stack = player.getMainHandItem();
			if(!(stack.getItem() instanceof AbacusItem))
				stack = player.getOffhandItem();

			if(stack.getItem() instanceof AbacusItem) {
				int distance = AbacusItem.getCount(stack, player);
				if(distance > -1 && distance <= AbacusItem.MAX_COUNT) {
					BlockPos target = AbacusItem.getBlockPos(stack);
					if (target != null) {

						Camera info = event.getCamera();
						Vec3 view = info.getPosition();

						VoxelShape shape = Shapes.create(new AABB(target));

						HitResult result = mc.hitResult;
						if (result != null && result.getType() == HitResult.Type.BLOCK) {
							BlockPos source = ((BlockHitResult) result).getBlockPos();

							int diffX = source.getX() - target.getX();
							int diffY = source.getY() - target.getY();
							int diffZ = source.getZ() - target.getZ();

							if (diffX != 0)
								shape = Shapes.or(shape, Shapes.create(new AABB(target).expandTowards(diffX, 0, 0)));
							if (diffY != 0)
								shape = Shapes.or(shape, Shapes.create(new AABB(target.offset(diffX, 0, 0)).expandTowards(0, diffY, 0)));
							if (diffZ != 0)
								shape = Shapes.or(shape, Shapes.create(new AABB(target.offset(diffX, diffY, 0)).expandTowards(0, 0, diffZ)));
						}

						if (shape != null) {
							List<AABB> list = shape.toAabbs();
							PoseStack poseStack = event.getPoseStack();

							// everything from here is a vanilla copy pasta but tweaked to have the same colors

							double xIn = -view.x;
							double yIn = -view.y;
							double zIn = -view.z;

							for (AABB aabb : list) {
								float r = (float) highlightColor.getElement(0);
								float g = (float) highlightColor.getElement(1);
								float b = (float) highlightColor.getElement(2);
								float a = (float) highlightColor.getElement(3);

								VoxelShape individual = Shapes.create(aabb.move(0.0D, 0.0D, 0.0D));
								PoseStack.Pose pose = poseStack.last();
								Matrix4f matrix4f = pose.pose();
								individual.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> {
									float f = (float) (maxX - minX);
									float f1 = (float) (maxY - minY);
									float f2 = (float) (maxZ - minZ);
									float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
									f /= f3;
									f1 /= f3;
									f2 /= f3;

									bufferIn.vertex(matrix4f, (float) (minX + xIn), (float) (minY + yIn), (float) (minZ + zIn)).color(r, g, b, a).normal(pose.normal(), f, f1, f2).endVertex();
									bufferIn.vertex(matrix4f, (float) (maxX + xIn), (float) (maxY + yIn), (float) (maxZ + zIn)).color(r, g, b, a).normal(pose.normal(), f, f1, f2).endVertex();
								});
							}

							event.setCanceled(true);
						}
					}
				}
			}
		}
	}

}
