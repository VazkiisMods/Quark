/*
 * The Cool MIT License (CMIT)
 *
 * Copyright (c) 2023 Emi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, as long as the person is cool, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * The person is cool.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.violetmoon.quark.content.automation.client.screen;

import java.util.List;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.automation.inventory.CrafterMenu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class CrafterScreen extends AbstractContainerScreen<CrafterMenu> {
	private static final ResourceLocation TEXTURE = Quark.asResource("textures/gui/container/crafter.png");

	public CrafterScreen(CrafterMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	public void init() {
		super.init();
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		renderBackground(context);
		super.render(context, mouseX, mouseY, delta);
		this.renderTooltip(context, mouseX, mouseY);
		if (menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.getItem().isEmpty() && this.hoveredSlot.container == menu.crafter && !isBlocked(this.hoveredSlot)) {
			context.renderComponentTooltip(font, List.of(
				Component.translatable("quark.misc.disable_slot")
			), mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.getItem().isEmpty() && this.hoveredSlot.container == menu.crafter) {
			this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, this.hoveredSlot.getContainerSlot());
		    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	public boolean isBlocked(Slot slot) {
		if (slot.container == menu.crafter && (menu.delegate.get(0) & (1 << (1 + slot.getContainerSlot()))) != 0) {
			return true;
		}
		return false;
	}

	@Override
	public void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		context.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
		int del = menu.delegate.get(0);
		if ((del & 1) != 0) {
			context.blit(TEXTURE, i + 97, j + 38, 176, 18, 16, 11);
		}
		for (int s = 0; s < 9; s++) {
			if ((del & (1 << (s + 1))) != 0) {
				Slot slot = menu.slots.get(s + 1);
				context.blit(TEXTURE, i + slot.x - 1, j + slot.y - 1, 176, 0, 18, 18);
			}
		}
	}
}
