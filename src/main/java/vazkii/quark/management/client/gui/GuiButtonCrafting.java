package vazkii.quark.management.client.gui;

import java.util.Arrays;

import com.google.common.base.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import vazkii.arl.util.RenderHelper;
import vazkii.quark.management.client.gui.GuiButtonChest.Action;
import vazkii.quark.management.feature.FavoriteItems;
import vazkii.quark.management.feature.StoreToChests;

public class GuiButtonCrafting extends GuiButton {

	public final Action action;


	public GuiButtonCrafting(Action action, int id, int par2, int par3) {
		super(id, par2, par3, 16, 16, "");
		this.action = action;
	}

	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
		if(enabled) {
			hovered = par2 >= xPosition && par3 >= yPosition && par2 < xPosition + width && par3 < yPosition + height;
			int k = getHoverState(hovered);

			int u = 0;
			int v = 64;

			if(k == 2)
				u += 16;
			if(action == Action.BALANCE)
				v += 16;
				
			
			par1Minecraft.renderEngine.bindTexture(GuiButtonChest.GENERAL_ICONS_RESOURCE);
			GlStateManager.color(1F, 1F, 1F, 1F);
			drawTexturedModalRect(xPosition, yPosition, u, v, 16, 16);
			
			if(k == 2) {
				GlStateManager.pushMatrix();
				String tooltip; 
				tooltip = I18n.translateToLocal("quarkmisc.craftButton." + action.name().toLowerCase());
				int len = Minecraft.getMinecraft().fontRendererObj.getStringWidth(tooltip);
				
				
				int tooltipShift = -len - 24;
				RenderHelper.renderTooltip(par2 + tooltipShift, par3 + 8, Arrays.asList(new String[] { tooltip }));
				GlStateManager.popMatrix();
			}
		}
	}

	public static enum Action {
		
		REDO,
		BALANCE
		
	}
}