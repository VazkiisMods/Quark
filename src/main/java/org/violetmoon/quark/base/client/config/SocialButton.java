package org.violetmoon.quark.base.client.config;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.QuarkClient;

import javax.annotation.Nonnull;
import java.util.List;

public class SocialButton extends Button {

	public static final ResourceLocation SOCIAL_ICONS = new ResourceLocation(Quark.MOD_ID, "textures/gui/social_icons.png");

	private final Component text;
	private final int textColor;
	private final int socialId;

	public SocialButton(int x, int y, Component text, int textColor, int socialId, OnPress onClick) {
		super(x, y, 20, 20, Component.literal(""), onClick);
		this.textColor = textColor;
		this.socialId = socialId;
		this.text = text;
	}

	public SocialButton(int x, int y, Component text, int textColor, int socialId, String url) {
		this(x, y, text, textColor, socialId, b -> Util.getPlatform().openUri(url));
	}

	@Override
	public void renderButton(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
		super.renderButton(mstack, mouseX, mouseY, partialTicks);

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, SOCIAL_ICONS);

		int u = socialId * 20;
		int v = isHovered ? 20 : 0;

		blit(mstack, x, y, u, v, 20, 20, 128, 64);

		if(isHovered)
			QuarkClient.ZETA_CLIENT.topLayerTooltipHandler.setTooltip(List.of(text.getString()), mouseX, mouseY);
	}

	@Override
	public int getFGColor() {
		return textColor;
	}

}
