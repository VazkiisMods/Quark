package vazkii.quark.management.feature;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageDeleteItem;
import vazkii.quark.management.client.gui.GuiButtonChest;
import vazkii.quark.management.client.gui.GuiButtonTrash;

public class DeleteItems extends Feature {

	boolean keyboardDown = false;
	boolean mouseDown = false;
	static GuiButtonTrash trash;

	boolean trashButton, playerInvOnly, needsShift;
	int trashButtonX, trashButtonY;

	@Override
	public void setupConfig() {
		trashButton = loadPropBool("Enable Trash Button", "", true);
		playerInvOnly = loadPropBool("Trash Button only on Player Inventory", "", false);
		trashButtonX = loadPropInt("Trash Button X", "", 3);
		trashButtonY = loadPropInt("Trash Button Y", "", -25);
		needsShift = loadPropBool("Trash Button Needs Shift", "", true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void initGui(GuiScreenEvent.InitGuiEvent.Post event) {
		trash = null;
		if(event.getGui() instanceof GuiContainer && trashButton) {
			GuiContainer guiInv = (GuiContainer) event.getGui();
			Container container = guiInv.inventorySlots;
			EntityPlayer player = Minecraft.getMinecraft().player;

			boolean isPlayerInv = guiInv instanceof GuiInventory;
			boolean creative = player.isCreative();
			if(creative || (!isPlayerInv && playerInvOnly))
				return;
			
			int guiWidth = guiInv.getXSize();
			int guiHeight = guiInv.getYSize();

			trash = new GuiButtonTrash(guiInv, 82424, guiWidth, guiHeight + trashButtonY, needsShift);
			event.getButtonList().add(trash);
			updateTrashPos(guiInv);
		}
	}

	@SubscribeEvent
	public void mouseEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
		boolean oldMouseDown = mouseDown;
		mouseDown = Mouse.isButtonDown(0);

		Minecraft mc = Minecraft.getMinecraft();
		GuiScreen current = Minecraft.getMinecraft().currentScreen;
		if(mouseDown != oldMouseDown && current instanceof GuiContainer) {
			GuiContainer gui = (GuiContainer) current;
			if(trash != null && trash.ready) {
				NetworkHandler.INSTANCE.sendToServer(new MessageDeleteItem(-1));
				event.setCanceled(true);
				mc.player.inventory.setItemStack(ItemStack.EMPTY);
			}
		}
	}

	@SubscribeEvent
	public void keyboardEvent(GuiScreenEvent.KeyboardInputEvent.Post event) {
		boolean down = Keyboard.isKeyDown(Keyboard.KEY_DELETE);
		if(GuiScreen.isCtrlKeyDown() && down && !this.keyboardDown && event.getGui() instanceof GuiContainer) {
			GuiContainer gui = (GuiContainer) event.getGui();
			Slot slot = gui.getSlotUnderMouse();
			if(slot != null) {
				IInventory inv = slot.inventory;
				if(inv instanceof InventoryPlayer) {
					int index = slot.getSlotIndex();

					if(Minecraft.getMinecraft().player.capabilities.isCreativeMode && index >= 36)
						index -= 36; // Creative mode messes with the indexes for some reason

					if(index < ((InventoryPlayer) inv).mainInventory.size())
						NetworkHandler.INSTANCE.sendToServer(new MessageDeleteItem(index));
				}
			}
		}
		this.keyboardDown = down;
	}


	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void update(ClientTickEvent event) {
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if(gui instanceof GuiContainer && trash != null) {
			GuiContainer inv = (GuiContainer) gui;
			updateTrashPos(inv);
		}
	}

	private void updateTrashPos(GuiContainer inv) {
		if(trash != null) {
			trash.x = inv.getGuiLeft() + trash.shiftX;
			trash.y = inv.getGuiTop() + trash.shiftY;
		}
	}

	public static void deleteItem(EntityPlayer player, int slot) {
		if(slot > player.inventory.mainInventory.size())
			return;

		ItemStack stack = slot == -1 ? player.inventory.getItemStack() : player.inventory.getStackInSlot(slot);
		if(!canItemBeDeleted(stack))
			return;

		if(slot == -1)
			player.inventory.setItemStack(ItemStack.EMPTY);
		else 
			player.inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
	}

	public static boolean canItemBeDeleted(ItemStack stack) {
		return ModuleLoader.isFeatureEnabled(DeleteItems.class) && !stack.isEmpty() && !FavoriteItems.isItemFavorited(stack);
	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

	@Override
	public String getFeatureIngameConfigName() {
		return "Delete Items";
	}

	@Nullable
	public static GuiButtonTrash getTrash() {
		return trash;
	}
}
