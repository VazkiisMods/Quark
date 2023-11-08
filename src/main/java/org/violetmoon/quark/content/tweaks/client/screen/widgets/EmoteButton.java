package org.violetmoon.quark.content.tweaks.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.violetmoon.quark.base.handler.MiscUtil;
import org.violetmoon.quark.content.tweaks.client.emote.EmoteDescriptor;
import org.violetmoon.quark.content.tweaks.module.EmotesModule;

import javax.annotation.Nonnull;

public class EmoteButton extends TranslucentButton {

	public final EmoteDescriptor desc;

	public EmoteButton(int x, int y, EmoteDescriptor desc, OnPress onPress) {
		super(x, y, EmotesModule.EMOTE_BUTTON_WIDTH - 1, EmotesModule.EMOTE_BUTTON_WIDTH - 1, Component.literal(""), onPress);
		this.desc = desc;
	}

	@Override
	public void renderButton(@Nonnull PoseStack matrix, int mouseX, int mouseY, float partial) {
		super.renderButton(matrix, mouseX, mouseY, partial);

		if(visible) {
			Minecraft mc = Minecraft.getInstance();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, desc.texture);
			blit(matrix, x + 4, y + 4, 0, 0, 16, 16, 16, 16);

			ResourceLocation tierTexture = desc.getTierTexture();
			if(tierTexture != null) {
				RenderSystem.setShaderTexture(0, tierTexture);
				blit(matrix, x + 4, y + 4, 0, 0, 16, 16, 16, 16);
			}

			boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			if(hovered) {
				String name = desc.getLocalizedName();
				MiscUtil.Client.drawChatBubble(matrix, x, y, mc.font, name, 1F, false);
			}
		}
	}

}
