/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [28/03/2016, 15:57:43 (GMT)]
 */
package vazkii.quark.management.feature;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.NetworkHandler;
import vazkii.quark.base.network.message.MessageDropoff;
import vazkii.quark.management.client.gui.GuiButtonChest;
import vazkii.quark.management.client.gui.GuiButtonChest.Action;

public class StoreToChests extends Feature {

	public static boolean invert;
	
	@Override
	public void setupConfig() {
		invert = loadPropBool("Invert button", "", false);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void initGui(GuiScreenEvent.InitGuiEvent.Post event) {
		if(event.getGui() instanceof GuiInventory) {
			GuiInventory guiInv = (GuiInventory) event.getGui();
			
	        int guiLeft = (guiInv.width - 176) / 2;
	        int guiTop = (guiInv.height - 166) / 2;
			
			Container container = guiInv.inventorySlots;
			for(Slot s : container.inventorySlots)
				if(s instanceof SlotCrafting) {
					event.getButtonList().add(new GuiButtonChest(Action.DROPOFF, 13211, guiLeft + s.xDisplayPosition, guiTop + s.yDisplayPosition + 30));
					break;
				}
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void performAction(GuiScreenEvent.ActionPerformedEvent.Pre event) {
		if(event.getButton() instanceof GuiButtonChest && ((GuiButtonChest) event.getButton()).action == Action.DROPOFF) {
			boolean smart = GuiScreen.isShiftKeyDown() != StoreToChests.invert;
			NetworkHandler.INSTANCE.sendToServer(new MessageDropoff(smart, false));				
		}
	}
	
	@Override
	public boolean hasSubscriptions() {
		return FMLCommonHandler.instance().getSide().isClient();
	}
	
}
