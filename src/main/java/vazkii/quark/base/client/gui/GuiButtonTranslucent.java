/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [27/03/2016, 00:02:53 (GMT)]
 */
package vazkii.quark.base.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiButtonTranslucent extends GuiButton {

	public GuiButtonTranslucent(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
	}

	@Override
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		drawRect(x, y, x + width, y + height, Integer.MIN_VALUE);
	}

	public void drawTexturedModalRect(int x, int y, int width, int height) {
		int textureX = 0;
		int textureY = 0;

		float f = 1F / 32F;
		float f1 = f;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(x + 0, y + height, zLevel).tex((textureX + 0) * f, (textureY + height) * f1).endVertex();
		vertexbuffer.pos(x + width, y + height, zLevel).tex((textureX + width) * f, (textureY + height) * f1).endVertex();
		vertexbuffer.pos(x + width, y + 0, zLevel).tex((textureX + width) * f, (textureY + 0) * f1).endVertex();
		vertexbuffer.pos(x + 0, y + 0, zLevel).tex((textureX + 0) * f, (textureY + 0) * f1).endVertex();
		tessellator.draw();
	}

}
