/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [02/04/2016, 17:54:27 (GMT)]
 */
package vazkii.quark.management.feature;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GLSync;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.handler.ItemNBTHelper;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.NetworkHandler;
import vazkii.quark.base.network.message.MessageFavoriteItem;
import vazkii.quark.management.client.gui.GuiButtonChest;
import vazkii.quark.management.client.gui.GuiButtonChest.Action;

public class FavoriteItems extends Feature {

	public static String TAG_FAVORITE_ITEM = "Quark:FavoriteItem";
	
	public static boolean hovering;
	boolean mouseDown = false;
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void keyboardEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
		boolean wasMouseDown = mouseDown;
		mouseDown = Mouse.isButtonDown(0);
		boolean click = mouseDown && !wasMouseDown;
		
		if(GuiScreen.isCtrlKeyDown() && click && event.getGui() instanceof GuiContainer) {
			GuiContainer gui = (GuiContainer) event.getGui();
			Slot slot = gui.getSlotUnderMouse();
			if(slot != null) {
				IInventory inv = slot.inventory;
				if(inv instanceof InventoryPlayer) {
					int index = slot.getSlotIndex();

					if(Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode && index >= 36)
						index -= 36; // Creative mode messes with the indexes for some reason
					
					if(index < ((InventoryPlayer) inv).mainInventory.length) {
						NetworkHandler.INSTANCE.sendToServer(new MessageFavoriteItem(index));
						event.setCanceled(true);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void drawEvent(GuiScreenEvent.DrawScreenEvent.Post event) {
		if(hovering && event.getGui() instanceof GuiContainer) {
			GuiContainer guiInv = (GuiContainer) event.getGui();
			Container container = guiInv.inventorySlots;
			
	        int guiLeft = (guiInv.width - 176) / 2;
	        int guiTop = (guiInv.height - 166) / 2;
			
			GlStateManager.color(1F, 1F, 1F);
			GlStateManager.pushMatrix();
			GlStateManager.disableDepth();
			GlStateManager.disableLighting();
			for(Slot s : container.inventorySlots) {
				ItemStack stack = s.getStack();
				if(isItemFavorited(stack)) {
					Minecraft.getMinecraft().renderEngine.bindTexture(GuiButtonChest.chest_icons);
					guiInv.drawTexturedModalRect(guiLeft + s.xDisplayPosition, guiTop + s.yDisplayPosition, 96, 0, 16, 16);
				}
			}
			GlStateManager.popMatrix();
		}
		
		hovering = false;
	}
	
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		if(isItemFavorited(event.getItemStack()))
			event.getToolTip().add(TextFormatting.GREEN + I18n.translateToLocal("quark.gui.favorited"));
	}
	
	public static void favoriteItem(EntityPlayer player, int slot) {
		if(!ModuleLoader.isFeatureEnabled(FavoriteItems.class) || slot >= player.inventory.getSizeInventory())
			return;
		
		ItemStack stack = player.inventory.getStackInSlot(slot);
		if(stack != null) {
			if(isItemFavorited(stack)) {
				NBTTagCompound cmp = stack.getTagCompound();
				cmp.removeTag(TAG_FAVORITE_ITEM);
				if(cmp.hasNoTags())
					stack.setTagCompound(null);
			} else ItemNBTHelper.setBoolean(stack, TAG_FAVORITE_ITEM, true);
		}
	}
	
	public static boolean isItemFavorited(ItemStack stack) {
		if(stack == null || !stack.hasTagCompound() || !ModuleLoader.isFeatureEnabled(FavoriteItems.class))
			return false;
		
		return ItemNBTHelper.getBoolean(stack, TAG_FAVORITE_ITEM, false);
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
