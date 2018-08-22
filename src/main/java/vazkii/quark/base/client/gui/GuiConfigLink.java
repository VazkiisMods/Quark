package vazkii.quark.base.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import vazkii.quark.base.lib.LibMisc;

public class GuiConfigLink extends GuiConfirmOpenLink {
	
	GuiScreen parent;

	public GuiConfigLink(GuiScreen parentScreenIn, String url) {
		super(parentScreenIn, url, 0, true);
		parent = parentScreenIn;
	}
	
	@Override 
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == 1) // Esc
			returnToParent();
	}
	
	void returnToParent() {
		mc.displayGuiScreen(parent);

		if(mc.currentScreen == null)
			mc.setIngameFocus();
	}

}
