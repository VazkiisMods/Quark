package org.violetmoon.quark.content.experimental.client.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import org.violetmoon.quark.content.experimental.module.VariantSelectorModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VariantsComponent implements ClientTooltipComponent, TooltipComponent {

	final ItemStack stack;

	boolean computed = false;
	List<ItemStack> variants;
	int height, width;

	public VariantsComponent(ItemStack stack) {
		this.stack = stack;
	}

	private List<ItemStack> getVariants() {
		if(computed)
			return variants;

		List<ItemStack> setVariants = new ArrayList<>();
		Item item = stack.getItem();
		if(item instanceof BlockItem bi) {
			Block block = bi.getBlock();

			Collection<Block> allVariants = VariantSelectorModule.variants.getAllVariants(block);
			for(Block b : allVariants)
				setVariants.add(new ItemStack(b));
		}

		computed = true;
		variants = setVariants;

		int size = variants.size();
		height = (size == 0 ? 0 : 20);
		width = size * 18 - 2;

		return variants;
	}

	@Override
	public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
		List<ItemStack> variants = getVariants();

		for(int i = 0; i < variants.size(); i++) {
			ItemStack variant = variants.get(i);
			guiGraphics.renderItem(variant, x + i * 18, y);
		}
	}

	@Override
	public int getHeight() {
		getVariants();
		return height;
	}

	@Override
	public int getWidth(@NotNull Font font) {
		getVariants();
		return width;
	}

}
