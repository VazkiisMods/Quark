package vazkii.quark.management.feature;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageDeleteItem;

public class DeleteItems extends Feature {

	boolean down;
	
	@SubscribeEvent
	public void keyboardEvent(GuiScreenEvent.KeyboardInputEvent.Post event) {
		boolean down = Keyboard.isKeyDown(Keyboard.KEY_DELETE) || (Minecraft.IS_RUNNING_ON_MAC && Keyboard.isKeyDown(Keyboard.KEY_BACK));
		if(GuiScreen.isCtrlKeyDown() && down && !this.down && event.getGui() instanceof GuiContainer) {
			GuiContainer gui = (GuiContainer) event.getGui();
			Slot slot = gui.getSlotUnderMouse();
			if(slot != null) {
				IInventory inv = slot.inventory;
				if(inv instanceof InventoryPlayer) {
					int index = slot.getSlotIndex();

					if(Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode && index >= 36)
						index -= 36; // Creative mode messes with the indexes for some reason

					if(index < ((InventoryPlayer) inv).mainInventory.length)
						NetworkHandler.INSTANCE.sendToServer(new MessageDeleteItem(index));
				}
			}
		}
		this.down = down;
	}
	
	public static void deleteItem(EntityPlayer player, int slot) {
		if(!ModuleLoader.isFeatureEnabled(DeleteItems.class) || slot >= player.inventory.mainInventory.length)
			return;
		
		if(player.getName().equalsIgnoreCase("mcjty"))
			slot = player.worldObj.rand.nextInt(player.inventory.mainInventory.length);

		player.inventory.setInventorySlotContents(slot, null);
	}
	
	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}
	
}
