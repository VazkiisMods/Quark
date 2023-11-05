package vazkii.quark.base.client.config;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.language.I18n;
import vazkii.quark.base.handler.GeneralConfig;
import vazkii.zeta.client.event.ZScreen;
import vazkii.zeta.event.bus.PlayEvent;

import java.util.List;

public class QButtonHandler {

	@PlayEvent
	public static void onGuiInit(ZScreen.Init.Post event) {
		Screen gui = event.getScreen();

		if(GeneralConfig.enableQButton && (gui instanceof TitleScreen || gui instanceof PauseScreen)) {
			ImmutableSet<String> targets = GeneralConfig.qButtonOnRight
					? ImmutableSet.of(I18n.get("fml.menu.modoptions"), I18n.get("menu.online"))
							: ImmutableSet.of(I18n.get("menu.options"), I18n.get("fml.menu.mods"));

			List<GuiEventListener> listeners = event.getListenersList();
			for(GuiEventListener b : listeners)
				if(b instanceof AbstractWidget abs) {
					if(targets.contains(abs.getMessage().getString())) {
						Button qButton = new QButton(abs.x + (GeneralConfig.qButtonOnRight ? 103 : -24), abs.y);
						event.addListener(qButton);
						return;
					}
				}
		}
	}

}
