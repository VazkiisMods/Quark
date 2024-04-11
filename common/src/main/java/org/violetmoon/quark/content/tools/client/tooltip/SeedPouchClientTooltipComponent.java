package org.violetmoon.quark.content.tools.client.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import org.violetmoon.quark.content.tools.item.SeedPouchItem;

public class SeedPouchClientTooltipComponent implements ClientTooltipComponent {

	private final ItemStack stack;
	private int width;

	public SeedPouchClientTooltipComponent(ItemStack stack) {
		this.stack = stack;

		SeedPouchItem.PouchContents contents = SeedPouchItem.getContents(stack);
		if(!contents.isEmpty()) {
			ItemStack seed = contents.getContents().copy();
			int count = contents.getCount();
			int stacks = Math.max(1, (count - 1) / seed.getMaxStackSize() + 1);

			width = stacks * 8 + 8;
		}
	}

	@Override
	public void renderImage(@NotNull Font font, int tooltipX, int tooltipY, @NotNull GuiGraphics guiGraphics) {
		SeedPouchItem.PouchContents contents = SeedPouchItem.getContents(stack);
		if(!contents.isEmpty()) {
			ItemStack seed = contents.getContents().copy();

			Minecraft mc = Minecraft.getInstance();

			int count = contents.getCount();
			int stacks = Math.max(1, (count - 1) / seed.getMaxStackSize() + 1);

			for(int i = 0; i < stacks; i++) {
				if(i == (stacks - 1))
					seed.setCount(count);

				int x = tooltipX + 8 * i;
				int y = tooltipY + (int) (Math.sin(i * 498543) * 2);

				guiGraphics.renderItem(seed, x, y);
				guiGraphics.renderItemDecorations(mc.font, seed, x, y);
			}
		}
	}

	@Override
	public int getHeight() {
		return width == 0 ? 0 : 20;
	}

	@Override
	public int getWidth(@NotNull Font font) {
		return width;
	}

}
