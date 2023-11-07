package vazkii.quark.content.client.tooltip;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.content.client.module.ImprovedTooltipsModule;
import vazkii.zeta.client.event.ZGatherTooltipComponents;

public class FuelTooltips {

	public static void makeTooltip(ZGatherTooltipComponents event) {
		ItemStack stack = event.getItemStack();
		if(!stack.isEmpty()) {
			Screen screen = Minecraft.getInstance().screen;
			if(screen != null && screen instanceof AbstractFurnaceScreen<?>) {
				int count = Quark.ZETA.itemExtensions.get(stack).getBurnTimeZeta(stack, RecipeType.SMELTING);
				if(count > 0) {
					Font font = Minecraft.getInstance().font;
					
					String time = getDisplayString(count);
					event.getTooltipElements().add(Either.right(new FuelComponent(stack, 18 + font.width(time), count)));
				}
			}
		}
	}
	
	private static String getDisplayString(int count) {
		float items = (float) count / (float) Math.max(1, ImprovedTooltipsModule.fuelTimeDivisor);
		String time = String.format(((items - (int) items) == 0) ? "x%.0f" : "x%.1f", items);
		return time;
	}

	public record FuelComponent(ItemStack stack, int width, int count) implements ClientTooltipComponent, TooltipComponent {

		@Override
		public void renderImage(@Nonnull Font font, int tooltipX, int tooltipY, @Nonnull PoseStack pose, @Nonnull ItemRenderer itemRenderer, int something) {
			pose.pushPose();
			pose.translate(tooltipX, tooltipY, 500);
		
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, MiscUtil.GENERAL_ICONS);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			GuiComponent.blit(pose, 1, 1, 0, 128, 13, 13, 256, 256);
			
			String time = getDisplayString(count);
			font.drawShadow(pose, time, 16, 5, 0xffb600);
			
			pose.popPose();			
		}

		@Override
		public int getHeight() {
			return 18;
		}

		@Override
		public int getWidth(@Nonnull Font font) {
			return width;
		}
	}
	
}
