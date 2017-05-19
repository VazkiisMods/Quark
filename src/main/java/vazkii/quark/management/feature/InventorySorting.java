package vazkii.quark.management.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.base.handler.SortingHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.network.message.MessageSortInventory;
import vazkii.quark.management.client.gui.GuiButtonChest;
import vazkii.quark.management.client.gui.GuiButtonChest.Action;

public class InventorySorting extends Feature {

	int xPos, yPos;
	int xPosC, yPosC;
	
	@Override
	public void setupConfig() {
		xPos = loadPropInt("Position X", "", -20);
		yPos = loadPropInt("Position Y ", "", 30);
		xPosC = loadPropInt("Position X (Creative)", "", 8);
		yPosC = loadPropInt("Position Y (Creative)", "", -20);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient(FMLPreInitializationEvent event) {
		ModKeybinds.initPlayerSortingKey();
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void initGui(GuiScreenEvent.InitGuiEvent.Post event) {
		if(event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiContainerCreative) {
			GuiContainer guiInv = (GuiContainer) event.getGui();
			GuiContainerCreative creativeInv = null;
			if(guiInv instanceof GuiContainerCreative)
				creativeInv = (GuiContainerCreative) guiInv;
			
			if(creativeInv == null && Minecraft.getMinecraft().player.capabilities.isCreativeMode)
				return;

			int guiLeft = (guiInv.width - 176) / 2;
			int guiTop = (guiInv.height - 166) / 2;

			Container container = guiInv.inventorySlots;
			for(Slot s : container.inventorySlots)
				if(creativeInv != null || s instanceof SlotCrafting) {
					if(creativeInv == null)
						ChestButtons.addButtonAndKeybind(event, Action.SORT, guiInv, 13212, guiLeft + s.xPos + xPos, guiTop + s.yPos + yPos, s, ModKeybinds.playerSortKey);
					else {
						if(s.getSlotIndex() != 15)
							continue;

						ChestButtons.<GuiContainerCreative>addButtonAndKeybind(event, Action.SORT, guiInv, 132112, guiLeft + s.xPos + xPosC, guiTop + s.yPos + yPosC, s, ModKeybinds.playerSortKey,
								(gui) -> gui.getSelectedTabIndex() == CreativeTabs.INVENTORY.getTabIndex());
					}

					break;
				}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void performAction(GuiScreenEvent.ActionPerformedEvent.Pre event) {
		if(event.getButton() instanceof GuiButtonChest) {
			Action a = ((GuiButtonChest) event.getButton()).action;
			if(a.isSortAction()) {
				boolean forcePlayer = a == Action.SORT_PLAYER;
				NetworkHandler.INSTANCE.sendToServer(new MessageSortInventory(forcePlayer));
				SortingHandler.sortInventory(Minecraft.getMinecraft().player, forcePlayer);
				event.setCanceled(true);
			}
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
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

	
}
