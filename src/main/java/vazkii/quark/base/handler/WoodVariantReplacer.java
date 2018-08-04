package vazkii.quark.base.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import vazkii.arl.util.ProxyRegistry;

public class WoodVariantReplacer {

	private static Function<ItemStack, Integer> compositeFunction = stack -> 0;
	
	public static void addReplacementFunction(Function<ItemStack, Integer> f) {
		Function<ItemStack, Integer> curr = compositeFunction;
		compositeFunction = stack -> {
				int res = f.apply(stack);
				return res == 0 ? curr.apply(stack) : res;
		};
	}
	
	public static void addReplacements(int size, Block... blocks) {
		for(Block b : blocks)
			addReplacementFunction(stack -> stack.getItem() == Item.getItemFromBlock(b) ? size : 0);
	}
	
	public static void addReplacements(Block... blocks) {
		addReplacements(1, blocks);
	}
	
	public static void executeReplacements() {
		List<ResourceLocation> recipeList = new ArrayList(CraftingManager.REGISTRY.getKeys());
		for(ResourceLocation res : recipeList) {
			IRecipe recipe = CraftingManager.REGISTRY.getObject(res);
			ItemStack out = recipe.getRecipeOutput();
			if(recipe instanceof ShapedRecipes && !out.isEmpty()) {
				int finalSize = compositeFunction.apply(out);
				if(finalSize > 0) {
					ShapedRecipes shaped = (ShapedRecipes) recipe;
					NonNullList<Ingredient> ingredients = shaped.recipeItems;
					for(int i = 0; i < ingredients.size(); i++) {
						Ingredient ingr = ingredients.get(i);
						if(ingr.apply(ProxyRegistry.newStack(Blocks.PLANKS)))
							ingredients.set(i, Ingredient.fromStacks(ProxyRegistry.newStack(Blocks.PLANKS, 1, 0)));
					}
					out.setCount(finalSize);
				}
			}
		}
		
	}
	
}

