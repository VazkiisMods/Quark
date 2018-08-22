package vazkii.quark.base.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import vazkii.quark.base.lib.LibMisc;

public class GuiConfigFirstLoad extends GuiConfigBase {

	public GuiConfigFirstLoad(GuiScreen parent) {
		super(parent);
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		title += " - " + I18n.translateToLocal("quark.config.firstload");

		int x = width / 2 - 100;
		int y = height / 6;
		
		buttonList.add(backButton = new GuiButton(0, x, y + 167, 200, 20, I18n.translateToLocal("quark.config.skip")));
		buttonList.add(new GuiButton(1, x, y + 142, 98, 20, I18n.translateToLocal("quark.config.configure")));
		buttonList.add(new GuiButton(2, x + 102, y + 142, 98, 20, I18n.translateToLocal("quark.config.opensite")));
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		switch(button.id) {
		case 1: // Configure
			mc.displayGuiScreen(new GuiConfigRoot(parent));
			break;
		case 2: // Import Config
			tryOpenWebsite(LibMisc.MOD_WEBSITE);
			break;
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		int x = width / 2;
		int y = height / 6;
		for(int i = 0; i < 10; i++) {
			String s = I18n.translateToLocal("quark.config.firstloadinfo" + i);
			if(i == 5)
				s = (TextFormatting.RED.toString() + TextFormatting.UNDERLINE.toString() + s);
			
			drawCenteredString(fontRenderer, s, x, y, 0xFFFFFF);
			
			y += 10;
			if(i == 2 || i == 4)
				y += 8;
		}
	}

}
