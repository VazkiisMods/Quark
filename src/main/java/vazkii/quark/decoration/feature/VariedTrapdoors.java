/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [24/03/2016, 17:13:26 (GMT)]
 */
package vazkii.quark.decoration.feature;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.block.BlockModTrapdoor;
import vazkii.quark.base.handler.RecipeHandler;
import vazkii.quark.base.module.Feature;

public class VariedTrapdoors extends Feature {

	public static Block spruce_trapdoor;
	public static Block birch_trapdoor;
	public static Block jungle_trapdoor;
	public static Block acacia_trapdoor;
	public static Block dark_oak_trapdoor;

	boolean renameVanillaTrapdoor;
	int recipeOutput;

	@Override
	public void setupConfig() {
		renameVanillaTrapdoor = loadPropBool("Rename vanilla trapdoor to Oak Trapdoor", "", true);
		recipeOutput = loadPropInt("Amount of trapdoors crafted (vanilla is 2)", "", 6);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		spruce_trapdoor = new BlockModTrapdoor("spruce_trapdoor");
		birch_trapdoor = new BlockModTrapdoor("birch_trapdoor");
		jungle_trapdoor = new BlockModTrapdoor("jungle_trapdoor");
		acacia_trapdoor = new BlockModTrapdoor("acacia_trapdoor");
		dark_oak_trapdoor = new BlockModTrapdoor("dark_oak_trapdoor");
	}

	@Override
	public void init(FMLInitializationEvent event) {
		List<IRecipe> recipeList = new ArrayList(CraftingManager.getInstance().getRecipeList());
		for(IRecipe recipe : recipeList) {
			ItemStack out = recipe.getRecipeOutput();
			if(out != null && out.getItem() == Item.getItemFromBlock(Blocks.trapdoor))
				CraftingManager.getInstance().getRecipeList().remove(recipe);
		}

		RecipeHandler.addOreDictRecipe(new ItemStack(Blocks.trapdoor, recipeOutput),
				"WWW", "WWW",
				'W', new ItemStack(Blocks.planks));

		RecipeHandler.addOreDictRecipe(new ItemStack(spruce_trapdoor, recipeOutput),
				"WWW", "WWW",
				'W', new ItemStack(Blocks.planks, 1, 1));
		RecipeHandler.addOreDictRecipe(new ItemStack(birch_trapdoor, recipeOutput),
				"WWW", "WWW",
				'W', new ItemStack(Blocks.planks, 1, 2));
		RecipeHandler.addOreDictRecipe(new ItemStack(jungle_trapdoor, recipeOutput),
				"WWW", "WWW",
				'W', new ItemStack(Blocks.planks, 1, 3));
		RecipeHandler.addOreDictRecipe(new ItemStack(acacia_trapdoor, recipeOutput),
				"WWW", "WWW",
				'W', new ItemStack(Blocks.planks, 1, 4));
		RecipeHandler.addOreDictRecipe(new ItemStack(dark_oak_trapdoor, recipeOutput),
				"WWW", "WWW",
				'W', new ItemStack(Blocks.planks, 1, 5));

		if(renameVanillaTrapdoor)
			Blocks.trapdoor.setUnlocalizedName("oak_trapdoor");
	}
}
