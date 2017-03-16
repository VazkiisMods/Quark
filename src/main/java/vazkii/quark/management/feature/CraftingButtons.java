package vazkii.quark.management.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
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
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.management.client.gui.GuiButtonCrafting;

public class CraftingButtons extends Feature {

	boolean redo, balance;
	int xShift, yShift;

	@Override
	public void setupConfig() {
		redo = loadPropBool("Enable Redo Button", "", true);
		balance = loadPropBool("Enable Balance Button", "", true);
		xShift = loadPropInt("Horizontal Icon Shift", "", -18);
		yShift = loadPropInt("Vertical Icon Shift", "", 0);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void initGui(GuiScreenEvent.InitGuiEvent.Post event) {
		if(event.getGui() instanceof GuiContainer) {
			GuiContainer guiInv = (GuiContainer) event.getGui();
			Container container = guiInv.inventorySlots;
			EntityPlayer player = Minecraft.getMinecraft().player;

			boolean accept = guiInv instanceof GuiCrafting;

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
					if(redo)
						event.getButtonList().add(new GuiButtonCrafting(GuiButtonCrafting.Action.REDO, 13210, guiLeft + xShift, guiTop + s.yPos - 60 + yShift));
					if(balance)
						event.getButtonList().add(new GuiButtonCrafting(GuiButtonCrafting.Action.BALANCE, 13211, guiLeft + xShift, guiTop + s.yPos - 40 + yShift));
					
					break;
				}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void performAction(GuiScreenEvent.ActionPerformedEvent.Pre event) {
		if(event.getButton() instanceof GuiButtonCrafting) {
			switch(((GuiButtonCrafting) event.getButton()).action) {
			case BALANCE:
				
			case REDO:
			}
		}
	}
	
	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}


}
