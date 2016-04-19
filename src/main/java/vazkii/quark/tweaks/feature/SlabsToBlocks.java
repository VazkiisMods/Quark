/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 03:23:49 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import vazkii.quark.base.handler.RecipeHandler;
import vazkii.quark.base.module.Feature;

public class SlabsToBlocks extends Feature {

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
				if(output != null && output.stackSize == originalSize) {
					Item outputItem = output.getItem();
					Block outputBlock = Block.getBlockFromItem(outputItem);
					if(outputBlock != null && outputBlock instanceof BlockSlab) {
						ItemStack outStack = null;

						for (Object recipeItem2 : recipeItems) {
							Object recipeItem = recipeItem2;
							if(recipeItem instanceof List) {
								List<ItemStack> ores = (List<ItemStack>) recipeItem;
								if(!ores.isEmpty())
									recipeItem = ores.get(0);
							}

							if(recipeItem != null) {
								outStack = (ItemStack) recipeItem;
								break;
							}
						}

						ItemStack outCopy = outStack.copy();
						if(outCopy.getItemDamage() == -1)
							outCopy.setItemDamage(0);

						RecipeHandler.addOreDictRecipe(outCopy,
								"B", "B",
								'B', output.copy());
					}
				}
			}
		}
	}

}
