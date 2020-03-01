package vazkii.quark.client.tooltip;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.handler.SimilarBlockTypeHandler;
import vazkii.quark.client.module.ChestSearchingModule;
import vazkii.quark.client.module.ImprovedTooltipsModule;

public class ShulkerBoxTooltips {

	public static final ResourceLocation WIDGET_RESOURCE = new ResourceLocation("quark", "textures/misc/shulker_widget.png");

	@OnlyIn(Dist.CLIENT)
	public static void makeTooltip(ItemTooltipEvent event) {
		if(SimilarBlockTypeHandler.isShulkerBox(event.getItemStack()) && event.getItemStack().hasTag()) {
			CompoundNBT cmp = ItemNBTHelper.getCompound(event.getItemStack(), "BlockEntityTag", true);
			if (cmp != null) {
				if (!cmp.contains("id", Constants.NBT.TAG_STRING)) {
					cmp = cmp.copy();
					cmp.putString("id", "minecraft:shulker_box");
				}
				TileEntity te = TileEntity.create(cmp);
				if (te != null && te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
					List<ITextComponent> tooltip = event.getToolTip();
					List<ITextComponent> tooltipCopy = new ArrayList<>(tooltip);

					for (int i = 1; i < tooltipCopy.size(); i++) {
						ITextComponent t = tooltipCopy.get(i);
						String s = t.getFormattedText();
						if (!s.startsWith("\u00a7") || s.startsWith("\u00a7o"))
							tooltip.remove(t);
					}

					if (ImprovedTooltipsModule.shulkerBoxRequireShift && !Screen.hasShiftDown())
						tooltip.add(1, new TranslationTextComponent("quark.misc.shulker_box_shift"));
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void renderTooltip(RenderTooltipEvent.PostText event) {
		if(SimilarBlockTypeHandler.isShulkerBox(event.getStack()) && event.getStack().hasTag() && (!ImprovedTooltipsModule.shulkerBoxRequireShift || Screen.hasShiftDown())) {
			Minecraft mc = Minecraft.getInstance();

			CompoundNBT cmp = ItemNBTHelper.getCompound(event.getStack(), "BlockEntityTag", true);
			if (cmp != null) {
				if (!cmp.contains("id", Constants.NBT.TAG_STRING)) {
					cmp = cmp.copy();
					cmp.putString("id", "minecraft:shulker_box");
				}
				TileEntity te = TileEntity.create(cmp);
				if (te != null) {
					LazyOptional<IItemHandler> handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
					handler.ifPresent((capability) -> {
						ItemStack currentBox = event.getStack();
						int currentX = event.getX() - 5;
						int currentY = event.getY() - 70;

						int size = capability.getSlots();
						int[] dims = { Math.min(size, 9), Math.max(size / 9, 1) };
						for (int[] testAgainst : TARGET_RATIOS) {
							if (testAgainst[0] * testAgainst[1] == size) {
								dims = testAgainst;
								break;
							}
						}

						int texWidth = CORNER * 2 + EDGE * dims[0];

						if (currentY < 0)
							currentY = event.getY() + event.getLines().size() * 10 + 5;

						int right = currentX + texWidth;
						MainWindow window = mc.getMainWindow();
						if (right > window.getScaledWidth())
							currentX -= (right - window.getScaledWidth());

						RenderSystem.pushMatrix();
						RenderHelper.enableStandardItemLighting();
						RenderSystem.enableRescaleNormal();
						RenderSystem.color3f(1F, 1F, 1F);
						RenderSystem.translatef(0, 0, 700);
						mc.getTextureManager().bindTexture(WIDGET_RESOURCE);

						RenderHelper.disableStandardItemLighting();

						int color = -1;

						if (ImprovedTooltipsModule.shulkerBoxUseColors && ((BlockItem) currentBox.getItem()).getBlock() instanceof ShulkerBoxBlock) {
							DyeColor dye = ((ShulkerBoxBlock) ((BlockItem) currentBox.getItem()).getBlock()).getColor();
							if (dye != null) {
								float[] colorComponents = dye.getColorComponentValues();
								color = ((int) (colorComponents[0] * 255) << 16) |
										((int) (colorComponents[1] * 255) << 8) |
										(int) (colorComponents[2] * 255);
							}
						}

						renderTooltipBackground(mc, currentX, currentY, dims[0], dims[1], color);

						ItemRenderer render = mc.getItemRenderer();

						RenderHelper.enableStandardItemLighting();
						RenderSystem.enableDepthTest();
						for (int i = 0; i < size; i++) {
							ItemStack itemstack = capability.getStackInSlot(i);
							int xp = currentX + 6 + (i % 9) * 18;
							int yp = currentY + 6 + (i / 9) * 18;

							if (!itemstack.isEmpty()) {
								render.renderItemAndEffectIntoGUI(itemstack, xp, yp);
								render.renderItemOverlays(mc.fontRenderer, itemstack, xp, yp);
							}

							if (!ChestSearchingModule.namesMatch(itemstack)) {
								RenderSystem.disableDepthTest();
								AbstractGui.fill(xp, yp, xp + 16, yp + 16, 0xAA000000);
							}
						}

						RenderSystem.disableDepthTest();
						RenderSystem.disableRescaleNormal();
						RenderSystem.popMatrix();
					});

				}
			}
		}
	}

	private static final int[][] TARGET_RATIOS = new int[][] {
			{ 1, 1 },
			{ 9, 3 },
			{ 9, 5 },
			{ 9, 6 },
			{ 9, 8 },
			{ 9, 9 },
			{ 12, 9 }
	};

	private static final int CORNER = 5;
	private static final int BUFFER = 1;
	private static final int EDGE = 18;


	public static void renderTooltipBackground(Minecraft mc, int x, int y, int width, int height, int color) {
		mc.getTextureManager().bindTexture(WIDGET_RESOURCE);
		RenderSystem.color3f(((color & 0xFF0000) >> 16) / 255f,
				((color & 0x00FF00) >> 8) / 255f,
				(color & 0x0000FF) / 255f);

		RenderHelper.disableStandardItemLighting();

		AbstractGui.blit(x, y,
				0, 0,
				CORNER, CORNER, 256, 256);
		AbstractGui.blit(x + CORNER + EDGE * width, y + CORNER + EDGE * height,
				CORNER + BUFFER + EDGE + BUFFER, CORNER + BUFFER + EDGE + BUFFER,
				CORNER, CORNER, 256, 256);
		AbstractGui.blit(x + CORNER + EDGE * width, y,
				CORNER + BUFFER + EDGE + BUFFER, 0,
				CORNER, CORNER, 256, 256);
		AbstractGui.blit(x, y + CORNER + EDGE * height,
				0, CORNER + BUFFER + EDGE + BUFFER,
				CORNER, CORNER, 256, 256);
		for (int row = 0; row < height; row++) {
			AbstractGui.blit(x, y + CORNER + EDGE * row,
					0, CORNER + BUFFER,
					CORNER, EDGE, 256, 256);
			AbstractGui.blit(x + CORNER + EDGE * width, y + CORNER + EDGE * row,
					CORNER + BUFFER + EDGE + BUFFER, CORNER + BUFFER,
					CORNER, EDGE, 256, 256);
			for (int col = 0; col < width; col++) {
				if (row == 0) {
					AbstractGui.blit(x + CORNER + EDGE * col, y,
							CORNER + BUFFER, 0,
							EDGE, CORNER, 256, 256);
					AbstractGui.blit(x + CORNER + EDGE * col, y + CORNER + EDGE * height,
							CORNER + BUFFER, CORNER + BUFFER + EDGE + BUFFER,
							EDGE, CORNER, 256, 256);
				}

				AbstractGui.blit(x + CORNER + EDGE * col, y + CORNER + EDGE * row,
						CORNER + BUFFER, CORNER + BUFFER,
						EDGE, EDGE, 256, 256);
			}
		}

		RenderSystem.color3f(1F, 1F, 1F);
	}

}
