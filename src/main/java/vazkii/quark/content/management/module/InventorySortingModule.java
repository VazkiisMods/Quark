package vazkii.quark.content.management.module;

import java.util.function.BooleanSupplier;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.client.handler.InventoryButtonHandler;
import vazkii.quark.base.client.handler.InventoryButtonHandler.ButtonProvider;
import vazkii.quark.base.client.handler.InventoryButtonHandler.ButtonTargetType;
import vazkii.quark.base.client.handler.ModKeybindHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.SortInventoryMessage;
import vazkii.quark.content.management.client.screen.widgets.MiniInventoryButton;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZKeyMapping;

@LoadModule(category = "management")
public class InventorySortingModule extends QuarkModule {

	@Config
	public static boolean enablePlayerInventory = true;
	@Config
	public static boolean enablePlayerInventoryInChests = true;
	@Config
	public static boolean enableChests = true;
	@Config(description = "Play a click when sorting inventories using keybindings")
	public static boolean satisfyingClick = true;

	@LoadEvent
	@OnlyIn(Dist.CLIENT)
	public void registerKeybinds(ZKeyMapping event) {
		KeyMapping sortPlayer = ModKeybindHandler.init(event, "sort_player", null, ModKeybindHandler.INV_GROUP);

		InventoryButtonHandler.addButtonProvider(this, ButtonTargetType.PLAYER_INVENTORY, 0,
				sortPlayer,
				(screen) -> {
					if (enablePlayerInventory) {
						if (satisfyingClick)
							click();
						QuarkNetwork.sendToServer(new SortInventoryMessage(true));
					}
				},
				provider("sort", true, () -> enablePlayerInventory),
				() -> enablePlayerInventory);
		
		InventoryButtonHandler.addButtonProvider(this, ButtonTargetType.CONTAINER_PLAYER_INVENTORY, 0,
				sortPlayer,
				(screen) -> {
					if (enablePlayerInventoryInChests) {
						if (satisfyingClick)
							click();
						QuarkNetwork.sendToServer(new SortInventoryMessage(true));
					}
				},
				provider("sort_inventory", true, () -> enablePlayerInventoryInChests),
				() -> enablePlayerInventoryInChests);
		
		InventoryButtonHandler.addButtonProvider(event, this, ButtonTargetType.CONTAINER_INVENTORY, 0,
				"sort_container",
				(screen) -> {
					if (enableChests) {
						if (satisfyingClick)
							click();
						QuarkNetwork.sendToServer(new SortInventoryMessage(false));
					}
				},
				provider("sort_container", false, () -> enableChests),
				() -> enableChests);
	}

	@OnlyIn(Dist.CLIENT)
	private ButtonProvider provider(String tooltip, boolean forcePlayer, BooleanSupplier condition) {
		return (parent, x, y) -> !condition.getAsBoolean() ? null :
				new MiniInventoryButton(parent, 0, x, y, "quark.gui.button." + tooltip, (b) -> QuarkNetwork.sendToServer(new SortInventoryMessage(forcePlayer)));
	}

	@OnlyIn(Dist.CLIENT)
	private void click() {
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}
}
