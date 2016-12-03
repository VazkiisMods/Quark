package vazkii.quark.management.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.handler.SortingHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.network.message.MessageSortInventory;
import vazkii.quark.management.client.gui.GuiButtonChest;
import vazkii.quark.management.client.gui.GuiButtonChest.Action;

public class InventorySorting extends Feature {

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void initGui(GuiScreenEvent.InitGuiEvent.Post event) {
		if(event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiContainerCreative) {
			GuiContainer guiInv = (GuiContainer) event.getGui();
			GuiContainerCreative creativeInv = null;
			if(guiInv instanceof GuiContainerCreative)
				creativeInv = (GuiContainerCreative) guiInv;

			int guiLeft = (guiInv.width - 176) / 2;
			int guiTop = (guiInv.height - 166) / 2;

			Container container = guiInv.inventorySlots;
			for(Slot s : container.inventorySlots)
				if(creativeInv != null || s instanceof SlotCrafting) {
					if(creativeInv == null)
						event.getButtonList().add(new GuiButtonChest(guiInv, Action.SORT, 13212, guiLeft + s.xDisplayPosition - 20, guiTop + s.yDisplayPosition + 30));
					else {
						if(s.getSlotIndex() != 15)
							continue;

						event.getButtonList().add(new GuiButtonChest<GuiContainerCreative>(creativeInv, Action.SORT, 13212, guiLeft + s.xDisplayPosition + 8, guiTop + s.yDisplayPosition - 20,
								(gui) -> gui.getSelectedTabIndex() == CreativeTabs.INVENTORY.getTabIndex()));
					}

					break;
				}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void performAction(GuiScreenEvent.ActionPerformedEvent.Pre event) {
		if(event.getButton() instanceof GuiButtonChest && ((GuiButtonChest) event.getButton()).action == Action.SORT) {
			NetworkHandler.INSTANCE.sendToServer(new MessageSortInventory());
			SortingHandler.sortInventory(Minecraft.getMinecraft().thePlayer);
			event.setCanceled(true);
		}
	}
	
	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}
	
	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "inventorytweaks", "inventorysorter", "itemscroller", "mousetweaks" };
	}

	
}
