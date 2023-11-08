package org.violetmoon.zeta.client.config.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.violetmoon.zeta.client.ZetaClient;

import javax.annotation.Nonnull;

public class PencilButton extends Button {

	//pencil: u32 v0 to u48 v16
	private final ResourceLocation iconsTexture;

	public PencilButton(ResourceLocation iconsTexture, int x, int y, OnPress pressable) {
		super(x, y, 20, 20, Component.literal(""), pressable);
		this.iconsTexture = iconsTexture;
	}

	public PencilButton(ZetaClient zc, int x, int y, OnPress pressable) {
		this(zc.generalIcons, x, y, pressable);
	}

	@Override
	public void renderButton(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
		super.renderButton(mstack, mouseX, mouseY, partialTicks);

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, iconsTexture);

		int u = 32;
		int v = 0;

		blit(mstack, x + 2, y + 1, u, v, 16, 16);
	}

}
