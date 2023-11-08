package org.violetmoon.quark.content.management.module;

import org.violetmoon.quark.base.client.handler.InventoryButtonHandler;
import org.violetmoon.quark.base.client.handler.InventoryButtonHandler.ButtonTargetType;
import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.network.QuarkNetwork;
import org.violetmoon.quark.base.network.message.InventoryTransferMessage;
import org.violetmoon.quark.content.management.client.screen.widgets.MiniInventoryButton;
import org.violetmoon.zeta.client.event.load.ZKeyMapping;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;

@ZetaLoadModule(category = "management")
public class EasyTransferingModule extends ZetaModule {

	public static boolean shiftLocked = false;

	@Config public static boolean enableShiftLock = true;

	@ZetaLoadModule(clientReplacement = true)
	public static class Client extends EasyTransferingModule {
		@LoadEvent
		public void registerKeybinds(ZKeyMapping event) {
			addButton(event, 1, "insert", false);
			addButton(event, 2, "extract", true);

			InventoryButtonHandler.addButtonProvider(event, this, ButtonTargetType.CONTAINER_PLAYER_INVENTORY, 3,
					"quark.keybind.shift_lock",
					(screen) -> shiftLocked = !shiftLocked,
					(parent, x, y) -> new MiniInventoryButton(parent, 4, x, y, "quark.gui.button.shift_lock",
							(b) -> shiftLocked = !shiftLocked)
							.setTextureShift(() -> shiftLocked),
					() -> enableShiftLock);
		}

		private void addButton(ZKeyMapping event, int priority, String name, boolean restock) {
			InventoryButtonHandler.addButtonProvider(event, this, ButtonTargetType.CONTAINER_PLAYER_INVENTORY, priority,
					"quark.keybind.transfer_" + name,
					(screen) -> QuarkNetwork.sendToServer(new InventoryTransferMessage(Screen.hasShiftDown(), restock)),
					(parent, x, y) -> new MiniInventoryButton(parent, priority, x, y,
							(t) -> t.add(I18n.get("quark.gui.button." + name + (Screen.hasShiftDown() ? "_filtered" : ""))),
							(b) -> QuarkNetwork.sendToServer(new InventoryTransferMessage(Screen.hasShiftDown(), restock)))
							.setTextureShift(Screen::hasShiftDown),
					null);
		}

		public static boolean hasShiftDown(boolean ret) {
			return ret || (enableShiftLock && shiftLocked);
		}
	}
}
