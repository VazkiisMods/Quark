package vazkii.quark.vanity.feature;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.module.Feature;

public class DyeItemNames extends Feature {

	private static final String TAG_DYE = "Quark:ItemNameDye";
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void makeTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack(); 
		if(!stack.isEmpty() && stack.hasTagCompound() && ItemNBTHelper.getInt(stack, TAG_DYE, -1) != -1) {
			FontRenderer font = Minecraft.getMinecraft().fontRendererObj; 
			int len = font.getStringWidth(stack.getDisplayName());
			String spaces = "";
			while(font.getStringWidth(spaces) < len)
				spaces += " ";
			
			event.getToolTip().set(0, spaces);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderTooltip(RenderTooltipEvent.PostText event) {
		ItemStack stack = event.getStack(); 
		if(stack != null && !stack.isEmpty() && stack.hasTagCompound()) {
			int dye = ItemNBTHelper.getInt(stack, TAG_DYE, -1);
			if(dye != -1) {
				int rgb = ItemDye.DYE_COLORS[Math.min(15, dye)];
				Color color = new Color(rgb);
				Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(stack.getDisplayName(), event.getX(), event.getY(), color.getRGB());
			}
		}
	}
	
	@SubscribeEvent
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();

		if (!left.isEmpty() && !right.isEmpty() && right.getItem() == Items.DYE) {
			ItemStack out = left.copy();
			String name = event.getName();
			if(!name.trim().isEmpty())
				out.setStackDisplayName(name.trim());
			
			ItemNBTHelper.setInt(out, TAG_DYE, right.getItemDamage());
			event.setOutput(out);
			event.setCost(3);
		}
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
