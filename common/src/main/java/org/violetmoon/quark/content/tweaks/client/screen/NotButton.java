package org.violetmoon.quark.content.tweaks.client.screen;

import org.violetmoon.quark.base.client.handler.ClientUtil;
import org.violetmoon.quark.content.tweaks.client.emote.EmoteDescriptor;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Vanilla btton stuff carries WAY too much baggage about keyboard focus
 *
 * - https://github.com/VazkiiMods/Quark/issues/4531
 * - https://github.com/VazkiiMods/Quark/issues/4436
 * - 7 billion other instances of this during testing
 */
public final class NotButton {
	private final int x;
	private final int y;
	private final int width;
	private final int height;
	private final Object label;
	private final Runnable onClick;

	private boolean oldMouseDown;

	public NotButton(int x, int y, int width, int height, Object label, Runnable onClick) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.label = label;
		this.onClick = onClick;
	}

	public void draw(GuiGraphics gfx, int mouseX, int mouseY, boolean mouseDown) {
		boolean hovered = (mouseX >= x) && (mouseY >= y) && (mouseX < x + width) && (mouseY < y + height);

		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();

		int bgColor = Minecraft.getInstance().options.getBackgroundColor(hovered ? 0xA0_000000 : 0x80_000000); //0x80_000000 == Integer.MIN_VALUE, see ChatScreen
		gfx.fill(x, y, x + width, y + height, bgColor);

		gfx.setColor(1, 1, 1, 1);
		if(label instanceof Component comp)
			drawTextLabel(gfx, comp);
		else if(label instanceof String s)
			drawTextLabel(gfx, Component.literal(s));
		else if(label instanceof EmoteDescriptor emote)
			drawEmote(gfx, emote, hovered);

		//clicks
		if(!oldMouseDown && mouseDown && hovered)
			onClick.run();
		oldMouseDown = mouseDown;
	}

	void drawTextLabel(GuiGraphics gfx, Component blah) {
		gfx.drawCenteredString(Minecraft.getInstance().font, blah, (x + x + width) / 2, ((y + y + height) / 2) - 4, 0xFFFFFFFF);
	}

	void drawEmote(GuiGraphics guiGraphics, EmoteDescriptor desc, boolean hovered) {
		Minecraft mc = Minecraft.getInstance();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		guiGraphics.blit(desc.texture, x + 4, y + 4, 0, 0, 16, 16, 16, 16);

		ResourceLocation tierTexture = desc.getTierTexture();
		if(tierTexture != null) {
			guiGraphics.blit(tierTexture, x + 4, y + 4, 0, 0, 16, 16, 16, 16);
		}

		if(hovered) {
			String name = desc.getLocalizedName();
			ClientUtil.drawChatBubble(guiGraphics, x, y, mc.font, name, 1F, false);
		}
	}

}
