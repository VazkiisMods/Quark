package org.violetmoon.quark.content.experimental.client.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.violetmoon.quark.content.experimental.module.VariantSelectorModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@OnlyIn(Dist.CLIENT)
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
	public void renderImage(Font font, int x, int y, PoseStack stack, ItemRenderer render, int yea) {
		List<ItemStack> variants = getVariants();
		
		for(int i = 0; i < variants.size(); i++) {
			ItemStack variant = variants.get(i);
			render.renderGuiItem(variant, x + i * 18, y);
		}
	}
	
	@Override
	public int getHeight() {
		getVariants();
		return height;
	}

	@Override
	public int getWidth(Font font) {
		getVariants();
		return width;
	}
	
}