/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [24/03/2016, 17:13:26 (GMT)]
 */
package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.recipe.BlacklistOreIngredient;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.block.BlockQuarkTrapdoor;
import vazkii.quark.base.handler.WoodVariantReplacer;
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
		spruce_trapdoor = new BlockQuarkTrapdoor("spruce_trapdoor");
		birch_trapdoor = new BlockQuarkTrapdoor("birch_trapdoor");
		jungle_trapdoor = new BlockQuarkTrapdoor("jungle_trapdoor");
		acacia_trapdoor = new BlockQuarkTrapdoor("acacia_trapdoor");
		dark_oak_trapdoor = new BlockQuarkTrapdoor("dark_oak_trapdoor");
	}

	@Override
	public void postPreInit(FMLPreInitializationEvent event) {		
		WoodVariantReplacer.addReplacements(recipeOutput, Blocks.TRAPDOOR);
		
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(spruce_trapdoor, recipeOutput),
				"WWW", "WWW",
				'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, 1));
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(birch_trapdoor, recipeOutput),
				"WWW", "WWW",
				'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, 2));
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(jungle_trapdoor, recipeOutput),
				"WWW", "WWW",
				'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, 3));
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(acacia_trapdoor, recipeOutput),
				"WWW", "WWW",
				'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, 4));
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(dark_oak_trapdoor, recipeOutput),
				"WWW", "WWW",
				'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, 5));

		if(renameVanillaTrapdoor)
			Blocks.TRAPDOOR.setUnlocalizedName("oak_trapdoor");

		// Low priority ore dictionary recipe
		Ingredient wood = new BlacklistOreIngredient("plankWood", (stack) -> stack.getItem() == Item.getItemFromBlock(Blocks.PLANKS));
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.TRAPDOOR, recipeOutput),
				"WWW", "WWW",
				'W', wood);
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		OreDictionary.registerOre("trapdoorWood", Blocks.TRAPDOOR);
		OreDictionary.registerOre("trapdoorWood", spruce_trapdoor);
		OreDictionary.registerOre("trapdoorWood", birch_trapdoor);
		OreDictionary.registerOre("trapdoorWood", jungle_trapdoor);
		OreDictionary.registerOre("trapdoorWood", acacia_trapdoor);
		OreDictionary.registerOre("trapdoorWood", dark_oak_trapdoor);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
}
