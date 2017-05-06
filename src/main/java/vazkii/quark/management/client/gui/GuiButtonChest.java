/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [28/03/2016, 15:59:35 (GMT)]
 */
package vazkii.quark.management.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import vazkii.arl.util.RenderHelper;
import vazkii.quark.base.client.IParentedGui;
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.management.feature.FavoriteItems;
import vazkii.quark.management.feature.StoreToChests;

public class GuiButtonChest<T extends GuiScreen> extends GuiButton implements IParentedGui {

	public static ResourceLocation GENERAL_ICONS_RESOURCE = new ResourceLocation("quark", "textures/misc/general_icons.png");
	public final Action action;
	public final T parent;

	Predicate<T> enabledPredicate = null;

	public GuiButtonChest(T parent, Action action, int id, int par2, int par3) {
		super(id, par2, par3, 16, 16, "");
		this.action = action;
		this.parent = parent;
	}

	public GuiButtonChest(T parent, Action action, int id, int par2, int par3, Predicate<T> enabledPredicate) {
		this(parent, action, id, par2, par3);
		this.enabledPredicate = enabledPredicate;
	}

	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
		if(enabledPredicate != null)
			enabled = enabledPredicate.apply(parent);

		if(enabled) {
			hovered = par2 >= xPosition && par3 >= yPosition && par2 < xPosition + width && par3 < yPosition + height;
			int k = getHoverState(hovered);

			int u = 0;
			int v = 0;

			switch(action) {
			case DROPOFF:
				if(GuiScreen.isShiftKeyDown() != StoreToChests.invert) {
					if(k == 2)
						u = 48;
					else u = 32;
				} else {
					if(k == 2)
						u = 16;
					else u = 0;
				}
				break;
			case SORT:
			case SORT_PLAYER:
				v = 16;
			case DEPOSIT:
				if(k == 2)
					u = 16;
				else u = 0;
				break;
			case SMART_DEPOSIT:
				if(k == 2)
					u = 48;
				else u = 32;
				break;
			case RESTOCK:
				if(k == 2)
					u = 80;
				else u = 64;
				break;
			}

			par1Minecraft.renderEngine.bindTexture(GENERAL_ICONS_RESOURCE);
			GlStateManager.color(1F, 1F, 1F, 1F);
			drawIcon(u, v);
			
			if(k == 2) {
				if(action != Action.RESTOCK && !action.isSortAction())
					FavoriteItems.hovering = true;
				
				GlStateManager.pushMatrix();
				String tooltip; 
				if(action == Action.DROPOFF && (GuiScreen.isShiftKeyDown() != StoreToChests.invert))
					tooltip = I18n.translateToLocal("quarkmisc.chestButton." + action.name().toLowerCase() + ".shift");
					else tooltip = I18n.translateToLocal("quarkmisc.chestButton." + action.name().toLowerCase());
				int len = Minecraft.getMinecraft().fontRendererObj.getStringWidth(tooltip);
				
				int tooltipShift = action == Action.DROPOFF ? 0 : -len - 24;
				
				List<String> tooltipList = new ArrayList();
				tooltipList.add(tooltip);
				BiMap<IParentedGui, KeyBinding> map = ModKeybinds.keyboundButtons.inverse();
				if(map.containsKey(this)) {
					KeyBinding key = map.get(this);
					if(key.getKeyCode() != 0) {
						String press = String.format(I18n.translateToLocal("quarkmisc.keyboundButton"), TextFormatting.GRAY, GameSettings.getKeyDisplayString(key.getKeyCode())); 
						tooltipList.add(press);
						
						if(action != Action.DROPOFF) {
							int len2 = Minecraft.getMinecraft().fontRendererObj.getStringWidth(press);
							if(len2 > len)
								tooltipShift = -len2 - 24;
						}
					}
				}
				
				RenderHelper.renderTooltip(par2 + tooltipShift, par3 + 8, tooltipList);
				GlStateManager.popMatrix();
			}
		}
	}
	
	protected void drawIcon(int u, int v) {
		drawTexturedModalRect(xPosition, yPosition, u, v, 16, 16);
	}
	
	@Override
	public GuiScreen getParent() {
		return parent;
	}

	public static enum Action {

		DROPOFF,
		DEPOSIT,
		SMART_DEPOSIT,
		RESTOCK,
		SORT,
		SORT_PLAYER;
		
		public boolean isSortAction() {
			return this == SORT || this == SORT_PLAYER;
		}

	}

}
