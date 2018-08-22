package vazkii.quark.base.client.gui;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleLoader;

public class GuiConfigRoot extends GuiConfigBase {
	
	boolean qEnabled;
	
	public GuiConfigRoot(GuiScreen parent) {
		super(parent);
		
		qEnabled = GlobalConfig.enableQButton;
	}

	@Override
	public void initGui() {
		super.initGui();

		int startX = width / 2 - 175;
		int startY = height / 6 - 12;

		int x = 0, y = 0, i = 0;

		Set<Module> modules = new TreeSet();
		modules.addAll(ModuleLoader.moduleInstances.values());

		for(Module module : modules) {
			x = startX + i % 2 * 180;
			y = startY + i / 2 * 22;

			buttonList.add(new GuiButtonModule(x, y, module));
			buttonList.add(new GuiButtonConfigSetting(x + 150, y, module.prop, false));

			i++;
		}

		x = width / 2;
		y = startY + 113;
		buttonList.add(new GuiButtonConfigSetting(x + 155, y, GlobalConfig.qButtonProp, true, I18n.translateToLocal("quark.config.enableq")));
		
		buttonList.add(new GuiButton(1, x - 100, y + 22, 98, 20, I18n.translateToLocal("quark.config.general")));
		buttonList.add(new GuiButton(2, x + 2, y + 22, 98, 20, I18n.translateToLocal("quark.config.import")));
		
		
		buttonList.add(new GuiButtonColor(3, x - 100, y + 44, 64, I18n.translateToLocal("quark.config.opensite"), 0x48ddbc));
		buttonList.add(new GuiButtonColor(4, x - 32, y + 44, 64, I18n.translateToLocal("quark.config.reddit"), 0x1f98e9));
		buttonList.add(new GuiButtonColor(5, x + 36, y + 44, 64, I18n.translateToLocal("quark.config.donate"), 0xf96854));
		
		buttonList.add(backButton = new GuiButton(0, x - 100, y + 66, 200, 20, I18n.translateToLocal("gui.done")));
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		String s = null;
		if(mayRequireRestart)
			s = I18n.translateToLocal("quark.config.needrestart");
		else if(qEnabled && !GlobalConfig.enableQButton)
			s = I18n.translateToLocal("quark.config.qdisabled");
		
		if(s != null)
			drawCenteredString(mc.fontRenderer, s, width / 2, backButton.y + 22, 0xFFFF00);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		if(button instanceof GuiButtonModule) {
			GuiButtonModule moduleButton = (GuiButtonModule) button;
			mc.displayGuiScreen(new GuiConfigModule(this, moduleButton.module));
		} 
		else switch(button.id) {
		case 1: // General Settings
			mc.displayGuiScreen(new GuiConfigCategory(this, "_global"));
			break;
		case 2: // Import Config
			mc.displayGuiScreen(new GuiConfigImport(this));
			break;
		case 3: // Open Quark Website
			tryOpenWebsite(LibMisc.MOD_WEBSITE);
			break;
		case 4: // Open Reddit
			tryOpenWebsite("https://old.reddit.com/r/QuarkMod/");
			break;
		case 5: // Open Donate Page
			tryOpenWebsite("https://vazkii.us/#donate");
			break;
		}
	}

}

