package org.violetmoon.quark.addons.oddities.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class MatrixEnchantingPlusButton extends Button {

	public MatrixEnchantingPlusButton(int x, int y, OnPress onPress) {
		super(x, y, 50, 12, Component.literal(""), onPress);
	}

	@Override
	public void render(@Nonnull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
		if(!visible)
			return;

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, MatrixEnchantingScreen.BACKGROUND);
		int u = 0;
		int v = 177;

		if(!active)
			v += 12;
		else if(hovered)
			v += 24;

		blit(stack, x, y, u, v, width, height);
	}

}
