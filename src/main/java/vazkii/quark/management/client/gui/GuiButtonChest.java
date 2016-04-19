/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [28/03/2016, 15:59:35 (GMT)]
 */
package vazkii.quark.management.client.gui;

import com.google.common.base.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.management.feature.FavoriteItems;
import vazkii.quark.management.feature.StoreToChests;

public class GuiButtonChest<T extends GuiScreen> extends GuiButton {

	public static ResourceLocation chest_icons = new ResourceLocation("quark", "textures/misc/chest_icons.png");
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

			if(k == 2 && action != Action.RESTOCK)
				FavoriteItems.hovering = true;

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

			par1Minecraft.renderEngine.bindTexture(chest_icons);
			GlStateManager.color(1F, 1F, 1F, 1F);
			drawTexturedModalRect(xPosition, yPosition, u, v, 16, 16);
		}
	}

	public static enum Action {

		DROPOFF,
		DEPOSIT,
		SMART_DEPOSIT,
		RESTOCK

	}

}
