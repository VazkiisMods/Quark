/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 03:23:49 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.base.module.Feature;

public class SlabsToBlocks extends Feature {

	public static Map<IBlockState, ItemStack> slabs = new HashMap();
	
	int originalSize;

	@Override
	public void setupConfig() {
		originalSize = loadPropInt("Vanilla stack size", "The stack size for the vanilla slab recipe, used for automatically detecting slab recipes", 6);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		List<IRecipe> recipeList = new ArrayList(CraftingManager.getInstance().getRecipeList());
		for(IRecipe recipe : recipeList) {
			if(recipe instanceof ShapedRecipes || recipe instanceof ShapedOreRecipe) {
				Object[] recipeItems;
				if(recipe instanceof ShapedRecipes)
					recipeItems = ((ShapedRecipes) recipe).recipeItems;
				else recipeItems = ((ShapedOreRecipe) recipe).getInput();

				ItemStack output = recipe.getRecipeOutput();
				if(!output.isEmpty() && output.getCount() == originalSize) {
					Item outputItem = output.getItem();
					Block outputBlock = Block.getBlockFromItem(outputItem);
					if(outputBlock != null && outputBlock instanceof BlockSlab) {
						ItemStack outStack = ItemStack.EMPTY;
						int inputItems = 0;

						for(Object recipeItem2 : recipeItems) {
							Object recipeItem = recipeItem2;
							if(recipeItem instanceof List) {
								List<ItemStack> ores = (List<ItemStack>) recipeItem;
								if(!ores.isEmpty())
									recipeItem = ores.get(0);
							}

							if(recipeItem != null && recipeItem instanceof ItemStack && !((ItemStack) recipeItem).isEmpty()) {
								ItemStack recipeStack = (ItemStack) recipeItem;
								if(outStack.isEmpty())
									outStack = recipeStack;
								
								if(ItemStack.areItemsEqual(outStack, recipeStack))
									inputItems++;
								else {
									outStack = ItemStack.EMPTY;
									break;
								}
							}
						}

						if(!outStack.isEmpty() && inputItems == 3) {
							ItemStack outCopy = outStack.copy();
							if(outCopy.getItemDamage() == OreDictionary.WILDCARD_VALUE)
								outCopy.setItemDamage(0);

							ItemStack in = output.copy();
							in.setCount(1);
							if(in.getItem() instanceof ItemBlock && outCopy.getItem() instanceof ItemBlock) {
								Block block = Block.getBlockFromItem(outCopy.getItem());
								slabs.put(block.getStateFromMeta(outCopy.getItemDamage()), in);
							}
							
							RecipeHandler.addShapelessOreDictRecipe(outCopy, in, in);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
