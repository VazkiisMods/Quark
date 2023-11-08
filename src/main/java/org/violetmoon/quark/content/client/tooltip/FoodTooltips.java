package org.violetmoon.quark.content.client.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.violetmoon.quark.content.client.module.ImprovedTooltipsModule;
import org.violetmoon.zeta.client.event.play.ZGatherTooltipComponents;

import javax.annotation.Nonnull;
import java.util.List;

public class FoodTooltips {

	private static boolean isPoison(FoodProperties food) {
		for (Pair<MobEffectInstance, Float> effect : food.getEffects()) {
			if (effect.getFirst() != null && effect.getFirst().getEffect() != null && effect.getFirst().getEffect().getCategory() == MobEffectCategory.HARMFUL) {
				return true;
			}
		}
		return false;
	}

	public static void makeTooltip(ZGatherTooltipComponents event, boolean showFood, boolean showSaturation) {
		ItemStack stack = event.getItemStack();
		if(stack.isEdible()) {
			FoodProperties food = stack.getItem().getFoodProperties();
			if (food != null) {
				int pips = food.getNutrition();
				if(pips == 0)
					return;

				int len = (int) Math.ceil((double) pips / ImprovedTooltipsModule.foodDivisor);

				int saturationSimplified = 0;
				float saturation = Math.min(20, food.getSaturationModifier() * food.getNutrition() * 2);
				if (saturation >= 19)
					saturationSimplified = 5;
				else if (saturation < 10) {
					if (saturation >= 8)
						saturationSimplified = 1;
					else if (saturation >= 6)
						saturationSimplified = 2;
					else if (saturation >= 2)
						saturationSimplified = 3;
					else
						saturationSimplified = 4;
				}

				String prefix = isPoison(food) ? "quark.misc.bad_saturation" : "quark.misc.saturation";

				Component saturationText = Component.translatable(prefix + saturationSimplified).withStyle(ChatFormatting.GRAY);
				List<Either<FormattedText, TooltipComponent>> tooltip = event.getTooltipElements();

				if (tooltip.isEmpty()) {
					if(showFood)
						tooltip.add(Either.right(new FoodComponent(stack, len, 10)));
					if(showSaturation)
						tooltip.add(Either.left(saturationText));
				}
				else {
					int i = 1;
					if(showFood) {
						tooltip.add(i, Either.right(new FoodComponent(stack, len, 10)));
						i++;
					}
					if(showSaturation)
						tooltip.add(i, Either.left(saturationText));
				}
			}
		}
	}

	public record FoodComponent(ItemStack stack, int width,
								int height) implements ClientTooltipComponent, TooltipComponent {

		@Override
		public void renderImage(@Nonnull Font font, int tooltipX, int tooltipY, @Nonnull PoseStack pose, @Nonnull ItemRenderer itemRenderer, int something) {
			if (stack.isEdible()) {
				FoodProperties food = stack.getItem().getFoodProperties();
				if (food != null) {
					RenderSystem.setShader(GameRenderer::getPositionTexShader);
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					Minecraft mc = Minecraft.getInstance();

					int pips = food.getNutrition();
					if (pips == 0)
						return;

					boolean poison = isPoison(food);

					int count = (int) Math.ceil((double) pips / ImprovedTooltipsModule.foodDivisor);
					boolean fract = pips % 2 != 0;
					int renderCount = count;
					int y = tooltipY - 1;

					boolean compress = count > ImprovedTooltipsModule.foodCompressionThreshold;
					if (compress) {
						renderCount = 1;
						if (fract)
							count--;
					}

					pose.pushPose();
					pose.translate(0, 0, 500);
					RenderSystem.setShader(GameRenderer::getPositionTexShader);
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);

					for (int i = 0; i < renderCount; i++) {
						int x = tooltipX + i * 9 - 1;

						int u = 16;
						if (poison)
							u += 117;
						int v = 27;

						GuiComponent.blit(pose, x, y, u, v, 9, 9, 256, 256);

						u = 52;
						if (fract && i == (renderCount - 1))
							u += 9;
						if (poison)
							u += 36;

						GuiComponent.blit(pose, x, y, u, v, 9, 9, 256, 256);
					}

					if (compress)
						mc.font.drawShadow(pose, "x" + (count + (fract ? ".5" : "")), tooltipX + 10, y + 1, 0xFF666666);
					pose.popPose();
				}
			}
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public int getWidth(@Nonnull Font font) {
			return width;
		}
	}

}
