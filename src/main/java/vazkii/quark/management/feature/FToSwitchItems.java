/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [31/03/2016, 02:35:05 (GMT)]
 */
package vazkii.quark.management.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.NetworkHandler;
import vazkii.quark.base.network.message.MessageSwapItems;

public class FToSwitchItems extends Feature {

	@SubscribeEvent
	public void keyboardEvent(GuiScreenEvent.KeyboardInputEvent.Post event) {
		if(GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSwapHands) && event.getGui() instanceof GuiContainer) {
			GuiContainer gui = (GuiContainer) event.getGui();
			Slot slot = gui.getSlotUnderMouse();
			if(slot != null) {
				IInventory inv = slot.inventory;
				if(inv instanceof InventoryPlayer) {
					int index = slot.getSlotIndex();

					if(Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode && index >= 36)
						index -= 36; // Creative mode messes with the indexes for some reason

					if(index < ((InventoryPlayer) inv).mainInventory.length)
						NetworkHandler.INSTANCE.sendToServer(new MessageSwapItems(index));
				}
			}
		}
	}

	public static void switchItems(EntityPlayer player, int slot) {
		if(!ModuleLoader.isFeatureEnabled(FToSwitchItems.class) || slot >= player.inventory.mainInventory.length)
			return;

		int offHandSlot = player.inventory.getSizeInventory() - 1;
		ItemStack stackAtSlot = player.inventory.getStackInSlot(slot);
		ItemStack stackAtOffhand = player.inventory.getStackInSlot(offHandSlot);

		player.inventory.setInventorySlotContents(slot, stackAtOffhand);
		player.inventory.setInventorySlotContents(offHandSlot, stackAtSlot);
	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

}
