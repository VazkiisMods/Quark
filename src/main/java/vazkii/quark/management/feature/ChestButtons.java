/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/04/2016, 17:04:11 (GMT)]
 */
package vazkii.quark.management.feature;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.google.common.base.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.base.handler.DropoffHandler;
import vazkii.quark.base.lib.LibObfuscation;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageDropoff;
import vazkii.quark.base.network.message.MessageRestock;
import vazkii.quark.management.client.gui.GuiButtonChest;
import vazkii.quark.management.client.gui.GuiButtonChest.Action;
import vazkii.quark.management.client.gui.GuiButtonShulker;

public class ChestButtons extends Feature {

	ButtonInfo deposit, smartDeposit, restock, sort, sortPlayer;
	
	boolean debugClassnames;
	List<String> classnames;
	
	@Override
	public void setupConfig() {
		deposit = loadButtonInfo("deposit", "", -18, -50);
		smartDeposit = loadButtonInfo("smart_deposit", "", -18, -30);
		restock = loadButtonInfo("restock", "", -18, 35);
		sort = loadButtonInfo("sort", "The Sort button is only available if the Inventory Sorting feature is enable", -18, -70);
		sortPlayer = loadButtonInfo("sort_player", "The Sort button is only available if the Inventory Sorting feature is enable", -18, 15);
		
		debugClassnames = loadPropBool("Debug Classnames", "Set this to true to print out the names of all GUIs you open to the log. This is used to fill in the \"Forced GUIs\" list.", false);
		String[] classnamesArr = loadPropStringList("Forced GUIs", "GUIs in which the chest buttons should be forced to show up. Use the \"Debug Classnames\" option to find the names.", new String[0]);
		classnames = new ArrayList(Arrays.asList(classnamesArr));
	}
	
	private ButtonInfo loadButtonInfo(String name, String comment, int xShift, int yShift) {
		ButtonInfo info = new ButtonInfo();
		String category = configCategory + "." + name;
		
		info.enabled = ModuleLoader.config.getBoolean("Enabled", category, true, comment); 
		info.xShift = ModuleLoader.config.getInt("X Position", category, xShift, Integer.MIN_VALUE, Integer.MAX_VALUE, "");
		info.yShift = ModuleLoader.config.getInt("Y Position", category, yShift, Integer.MIN_VALUE, Integer.MAX_VALUE, "");
		return info;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient(FMLPreInitializationEvent event) {
		ModKeybinds.initChestKeys();
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void initGui(GuiScreenEvent.InitGuiEvent.Post event) {
		if(event.getGui() instanceof GuiContainer) {
			GuiContainer guiInv = (GuiContainer) event.getGui();
			Container container = guiInv.inventorySlots;
			EntityPlayer player = Minecraft.getMinecraft().player;

			if(debugClassnames)
				FMLLog.log(Level.INFO, "[Quark] Opening GUI %s", guiInv.getClass().getName());
			
			boolean accept = guiInv instanceof GuiChest || guiInv instanceof GuiShulkerBox || classnames.contains(guiInv.getClass().getName());
			
			if(!accept)
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
					addButtonAndKeybind(event, restock, Action.RESTOCK, guiInv, 13211, guiLeft, guiTop, s, ModKeybinds.chestRestockKey);
					addButtonAndKeybind(event, deposit, Action.DEPOSIT, guiInv, 13212, guiLeft, guiTop, s, ModKeybinds.chestDropoffKey);
					addButtonAndKeybind(event, smartDeposit, Action.SMART_DEPOSIT, guiInv, 13213, guiLeft, guiTop, s, ModKeybinds.chestMergeKey);
					
					if(ModuleLoader.isFeatureEnabled(InventorySorting.class)) {
						addButtonAndKeybind(event, sort, Action.SORT, guiInv, 13214, guiLeft, guiTop, s, ModKeybinds.chestSortKey);
						addButtonAndKeybind(event, sort, Action.SORT_PLAYER, guiInv, 13215, guiLeft, guiTop, s, ModKeybinds.playerSortKey);
					}
					
					break;
				}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void addButtonAndKeybind(GuiScreenEvent.InitGuiEvent.Post event, ButtonInfo info, Action action, GuiContainer guiInv, int index, int guiLeft, int guiTop, Slot s, KeyBinding kb) {
		if(info.enabled)
			addButtonAndKeybind(event, action, guiInv, index, guiLeft + info.xShift, guiTop + s.yPos + info.yShift, s, kb);
	}

	@SideOnly(Side.CLIENT)
	public static void addButtonAndKeybind(GuiScreenEvent.InitGuiEvent.Post event, Action action, GuiContainer guiInv, int index, int x, int y, Slot s, KeyBinding kb) {
		addButtonAndKeybind(event, action, guiInv, index, x, y, s, kb, null);
	}

	@SideOnly(Side.CLIENT)
	public static <T extends GuiScreen>void addButtonAndKeybind(GuiScreenEvent.InitGuiEvent.Post event, Action action, GuiContainer guiInv, int index, int x, int y, Slot s, KeyBinding kb, Predicate<T> pred) {
		GuiButtonChest button;
		if(guiInv instanceof GuiShulkerBox)
			button = new GuiButtonShulker((GuiShulkerBox) guiInv, action, index, x, y);
		else button = new GuiButtonChest(guiInv, action, index, x, y, pred);
		
		event.getButtonList().add(button);
		if(kb != null)
			ModKeybinds.keybindButton(kb, button);
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
				event.setCanceled(true);
				break;
			case DEPOSIT:
				NetworkHandler.INSTANCE.sendToServer(new MessageDropoff(false, true));
				event.setCanceled(true);
				break;
			case RESTOCK:
				NetworkHandler.INSTANCE.sendToServer(new MessageRestock());
				event.setCanceled(true);
				break;
			}
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	private static class ButtonInfo {
		boolean enabled;
		int xShift, yShift;
	}

}
