package vazkii.quark.base.client.gui;

import java.util.List;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.ModuleLoader;

public final class ConfigEvents {

	@SubscribeEvent
	public static void onGuiOpen(GuiOpenEvent event) {
		if(ModuleLoader.firstLoad && event.getGui() instanceof GuiMainMenu) {
			ModuleLoader.firstLoad = false;
			event.setGui(new GuiConfigFirstLoad(event.getGui()));
		}
	}
	
	@SubscribeEvent
	public static void onGuiInit(GuiScreenEvent.InitGuiEvent event) {
		GuiScreen gui = event.getGui();
		
		if(GlobalConfig.enableQButton && (gui instanceof GuiMainMenu || gui instanceof GuiIngameMenu)) {
			ImmutableSet<String> targets = GlobalConfig.qButtonOnRight 
					? ImmutableSet.of(I18n.format("fml.menu.modoptions"), I18n.format("menu.online").replace("Minecraft", "").trim())
					: ImmutableSet.of(I18n.format("menu.options"), I18n.format("fml.menu.mods"));
					
			List<GuiButton> buttons = event.getButtonList();
			for(GuiButton b : buttons)
				if(targets.contains(b.displayString)) {
					GuiButton qButton = new GuiButtonColor(-82392, b.x + (GlobalConfig.qButtonOnRight ? 103 : -24), b.y, 20, "q", 0x48ddbc);
					buttons.add(qButton);
					return;
				}
		}
	}
	
	@SubscribeEvent
	public static void onButtonClick(GuiScreenEvent.ActionPerformedEvent event) {
		if(event.getButton() instanceof GuiButtonColor)
			Minecraft.getMinecraft().displayGuiScreen(new GuiConfigRoot(event.getGui()));
	}
	
}
