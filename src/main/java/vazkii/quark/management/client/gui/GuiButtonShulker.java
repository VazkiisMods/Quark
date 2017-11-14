package vazkii.quark.management.client.gui;

import java.awt.Color;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotShulkerBox;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.math.BlockPos;

public class GuiButtonShulker extends GuiButtonChest<GuiShulkerBox> {

	public GuiButtonShulker(GuiShulkerBox parent, Action action, int id, int par2, int par3, int left, int top) {
		super(parent, action, id, par2, par3, left, top);
	}

	@Override
	protected void drawChest() {
		Minecraft mc = Minecraft.getMinecraft();
		BlockPos pos = mc.objectMouseOver.getBlockPos();
		if(pos != null) {
			TileEntity tile = mc.world.getTileEntity(pos);
			if(tile instanceof TileEntityShulkerBox) {
				TileEntityShulkerBox shulker = (TileEntityShulkerBox) tile;
				EnumDyeColor dye = ((BlockShulkerBox) shulker.getBlockType()).getColor();
				int color = ItemDye.DYE_COLORS[dye.getDyeDamage()];
				Color colorObj = new Color(color).brighter();
				GlStateManager.color((float) colorObj.getRed() / 255F, (float) colorObj.getGreen() / 255F, (float) colorObj.getBlue() / 255F);
				super.drawIcon(16, 128);
				GlStateManager.color(1F, 1F, 1F);
				return;
			}
		}

		super.drawChest();
	}

}
