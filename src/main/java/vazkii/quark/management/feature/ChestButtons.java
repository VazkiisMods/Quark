/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [02/04/2016, 17:04:11 (GMT)]
 */
package vazkii.quark.management.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.handler.DropoffHandler;
import vazkii.quark.base.lib.LibObfuscation;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.network.NetworkHandler;
import vazkii.quark.base.network.message.MessageDropoff;
import vazkii.quark.base.network.message.MessageRestock;
import vazkii.quark.management.client.gui.GuiButtonChest;
import vazkii.quark.management.client.gui.GuiButtonChest.Action;

public class ChestButtons extends Feature {

	boolean deposit, smartDeposit, restock;

	@Override
	public void setupConfig() {
		deposit = loadPropBool("Enable Deposit Button", "", true);
		smartDeposit = loadPropBool("Enable Smart Deposit Button", "", true);
		restock = loadPropBool("Enable Restock Button", "", true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void initGui(GuiScreenEvent.InitGuiEvent.Post event) {
		if(event.getGui() instanceof GuiContainer) {
			GuiContainer guiInv = (GuiContainer) event.getGui();
			Container container = guiInv.inventorySlots;
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;

			boolean accept = guiInv instanceof GuiChest;

			for(Slot s : container.inventorySlots) {
				IInventory inv = s.inventory;
				if(inv != null && DropoffHandler.isValidChest(player, inv)) {
					accept = true;
					break;
				}
			}

			if(!accept)
				return;

			int guiLeft = ReflectionHelper.getPrivateValue(GuiContainer.class, guiInv, LibObfuscation.GUI_LEFT);
			int guiTop = ReflectionHelper.getPrivateValue(GuiContainer.class, guiInv, LibObfuscation.GUI_TOP);

			for(Slot s : container.inventorySlots)
				if(s.inventory == player.inventory && s.getSlotIndex() == 9) {
					if(restock)
						event.getButtonList().add(new GuiButtonChest(guiInv, Action.RESTOCK, 13211, guiLeft - 18, guiTop + s.yDisplayPosition));
					if(deposit)
						event.getButtonList().add(new GuiButtonChest(guiInv, Action.DEPOSIT, 13212, guiLeft - 18, guiTop + s.yDisplayPosition + 18));
					if(smartDeposit)
						event.getButtonList().add(new GuiButtonChest(guiInv, Action.SMART_DEPOSIT, 13213, guiLeft - 18, guiTop + s.yDisplayPosition + 36));

					break;
				}
		}
	}

	@SuppressWarnings("incomplete-switch")
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void performAction(GuiScreenEvent.ActionPerformedEvent.Pre event) {
		if(event.getButton() instanceof GuiButtonChest) {
			GuiButtonChest buttonChest = (GuiButtonChest) event.getButton();
			Action action = buttonChest.action;

			switch(action) {
			case SMART_DEPOSIT:
				NetworkHandler.INSTANCE.sendToServer(new MessageDropoff(true, true));
				break;
			case DEPOSIT:
				NetworkHandler.INSTANCE.sendToServer(new MessageDropoff(false, true));
				break;
			case RESTOCK:
				NetworkHandler.INSTANCE.sendToServer(new MessageRestock());
				break;
			}

			event.setCanceled(true);
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

}
