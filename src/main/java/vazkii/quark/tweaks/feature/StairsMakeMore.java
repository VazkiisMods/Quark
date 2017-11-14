/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 03:15:51 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import vazkii.arl.recipe.MultiRecipe;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.quark.base.module.Feature;

public class StairsMakeMore extends Feature {

	public static Map<IBlockState, ItemStack> stairs = new HashMap();
	
	int targetSize;
	int originalSize;
	boolean reversionRecipe;
	boolean enableSlabToStair;
	
	private MultiRecipe slabMultiRecipe, returnMultiRecipe;

	@Override
	public void setupConfig() {
		targetSize = loadPropInt("Target stack size (must be a divisor of 24 if 'Reversion recipe' is enabled)", "", 8);
		originalSize = loadPropInt("Vanilla stack size", "The stack size for the vanilla stair recipe, used for automatically detecting stair recipes", 4);
		reversionRecipe = loadPropBool("Add stairs to blocks recipe", "", true);
		enableSlabToStair = loadPropBool("Enable Slab to Stairs Recipe", "This recipe can only be enabled if the \"Slabs to blocks recipe\" feature is.", true);
	}
	
	@Override
	public void postPreInit(FMLPreInitializationEvent event) {
		if(enableSlabToStair)
			slabMultiRecipe = new MultiRecipe(new ResourceLocation("quark", "slabs_to_stairs"));
		if(reversionRecipe)
			returnMultiRecipe = new MultiRecipe(new ResourceLocation("quark", "stairs_to_blocks"));
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		List<ResourceLocation> recipeList = new ArrayList(CraftingManager.REGISTRY.getKeys());
		for(ResourceLocation res : recipeList) {
			IRecipe recipe = CraftingManager.REGISTRY.getObject(res);
			ItemStack output = recipe.getRecipeOutput();
			if(!output.isEmpty() && output.getCount() == originalSize) {
				Item outputItem = output.getItem();
				Block outputBlock = Block.getBlockFromItem(outputItem);
				if(outputBlock != null && outputBlock instanceof BlockStairs) {
					output.setCount(targetSize);

					if(recipe instanceof ShapedRecipes || recipe instanceof ShapedOreRecipe) {
						NonNullList<Ingredient> recipeItems;
						if(recipe instanceof ShapedRecipes)
							recipeItems = ((ShapedRecipes) recipe).recipeItems;
						else recipeItems = ((ShapedOreRecipe) recipe).getIngredients();

						ItemStack outStack = ItemStack.EMPTY;
						int inputItems = 0;

						for(Ingredient ingredient : recipeItems) {
							ItemStack recipeItem = ItemStack.EMPTY;
							ItemStack[] matches = ingredient.getMatchingStacks();
							if(matches.length > 0)
								recipeItem = matches[0];
							
							if(recipeItem != null && !((ItemStack) recipeItem).isEmpty()) {
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

						if(!outStack.isEmpty() && inputItems == 6) {
							ItemStack outCopy = outStack.copy();
							if(outCopy.getItemDamage() == OreDictionary.WILDCARD_VALUE)
								outCopy.setItemDamage(0);

							outCopy.setCount(24 / targetSize);
							ItemStack in = output.copy();
							in.setCount(1);
							if(in.getItem() instanceof ItemBlock && outCopy.getItem() instanceof ItemBlock) {
								Block block = Block.getBlockFromItem(outCopy.getItem());
								stairs.put(block.getStateFromMeta(outCopy.getItemDamage()), in);
							}
							
							if(reversionRecipe)
								RecipeHandler.addShapelessOreDictRecipe(returnMultiRecipe, outCopy, in, in, in, in);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void finalInit(FMLPostInitializationEvent event) {
		if(enableSlabToStair && !stairs.isEmpty() && !SlabsToBlocks.slabs.isEmpty())
			for(IBlockState state : stairs.keySet()) 			
				if(SlabsToBlocks.slabs.containsKey(state)) {
					ItemStack stair = stairs.get(state).copy();
					if(!stair.isEmpty()) {
						stair.setCount(targetSize / 2);
						ItemStack slab = SlabsToBlocks.slabs.get(state);
						
						RecipeHandler.addOreDictRecipe(slabMultiRecipe, stair, 
								"S  ", "SS ", "SSS",
								'S', slab);
					}
				}
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
