/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [27/03/2016, 00:14:28 (GMT)]
 */
package vazkii.quark.vanity.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.base.client.gui.GuiButtonTranslucent;

public class GuiButtonEmote extends GuiButtonTranslucent {

	public final String emote;
	private ResourceLocation resource;

	public GuiButtonEmote(int buttonId, int x, int y, String emote) {
		super(buttonId, x, y, 50, 50, I18n.format("quark.emote." + emote));
		this.emote = emote;
		resource = new ResourceLocation("quark", "textures/emotes/" + emote + ".png");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		super.drawButton(mc, mouseX, mouseY);

		if(visible) {
			GlStateManager.color(1F, 1F, 1F);
			mc.getTextureManager().bindTexture(resource);
			drawTexturedModalRect(xPosition + 9, yPosition + 4, 32, 32);
		}
	}

	@Override
	public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		super.drawCenteredString(fontRendererIn, text, x, y + 16, color);
	}

}
