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
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.handler.DropoffHandler;
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
				if(DropoffHandler.isValidChest(player, inv) && inv instanceof TileEntity) {
					accept = true;
					break;
				}
			}
			
			if(!accept)
				return;
			
	        int guiLeft = (guiInv.width - 176) / 2;
	        int guiTop = (guiInv.height - 166) / 2;
			
			for(Slot s : container.inventorySlots)
				if(s.inventory == player.inventory && s.getSlotIndex() == 9) {
					event.getButtonList().add(new GuiButtonChest(Action.RESTOCK, 13211, guiLeft + s.xDisplayPosition - 25, guiTop + s.yDisplayPosition));
					event.getButtonList().add(new GuiButtonChest(Action.DEPOSIT, 13212, guiLeft + s.xDisplayPosition - 25, guiTop + s.yDisplayPosition + 18));
					event.getButtonList().add(new GuiButtonChest(Action.SMART_DEPOSIT, 13213, guiLeft + s.xDisplayPosition - 25, guiTop + s.yDisplayPosition + 36));
					
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
		}
	}
	
	@Override
	public boolean hasSubscriptions() {
		return FMLCommonHandler.instance().getSide().isClient();
	}
	
}
