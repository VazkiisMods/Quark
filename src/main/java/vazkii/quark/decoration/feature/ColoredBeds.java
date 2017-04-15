/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [03/07/2016, 22:24:37 (GMT)]
 */
package vazkii.quark.decoration.feature;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockColoredBed;
import vazkii.quark.decoration.item.ItemColoredBed;

public class ColoredBeds extends Feature {

	public static Block[] colored_bed_blocks;
	public static Item colored_bed_item;

	boolean renameVanillaBed;
	int defaultColor;
	public static int stackSize;

	@Override
	public void setupConfig() {
		renameVanillaBed = loadPropBool("Rename vanilla bed to Red Bed", "", true);
		defaultColor = loadPropInt("Default Color", "The default color of bed to be created if wool types are mixed. 0 is white, 15 is black, 14 is vanilla red bed.", 0);
		stackSize = loadPropInt("Bed Stack Size", "This also changes the stack size of the vanilla bed.", 64);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		colored_bed_blocks = new Block[15];

		for(int i = 0; i < 15; i++) {
			EnumDyeColor color = EnumDyeColor.class.getEnumConstants()[i];
			if(color == EnumDyeColor.RED)
				color = EnumDyeColor.BLACK;

			colored_bed_blocks[i] = new BlockColoredBed("colored_bed_" + color.getName(), i);
		}

		colored_bed_item = new ItemColoredBed();
		
		OreDictionary.registerOre("bed", new ItemStack(colored_bed_item, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("bed", Items.BED);
		for(Block b : colored_bed_blocks)
			OreDictionary.registerOre("blockBed", b);
		OreDictionary.registerOre("blockBed", Blocks.BED);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		Items.BED.setUnlocalizedName("red_bed");
		Items.BED.setMaxStackSize(stackSize);

		List<IRecipe> recipeList = new ArrayList(CraftingManager.getInstance().getRecipeList());
		for(IRecipe recipe : recipeList) {
			ItemStack out = recipe.getRecipeOutput();
			if(!out.isEmpty() && out.getItem() == Items.BED)
				CraftingManager.getInstance().getRecipeList().remove(recipe);
		}

		for(int i = 0; i < 16; i++) {
			ItemStack stack;
			if(i == 14)
				stack = new ItemStack(Items.BED);
			else stack = new ItemStack(colored_bed_item, 1, Math.min(14, i));

			RecipeHandler.addOreDictRecipe(stack,
					"WWW", "PPP",
					'W', new ItemStack(Blocks.WOOL, 1, i),
					'P', "plankWood");
			RecipeHandler.addShapelessOreDictRecipe(stack, "bed", LibMisc.OREDICT_DYES.get(15 - i));
		}


		ItemStack defaultStack = new ItemStack(colored_bed_item, 1, Math.min(14, defaultColor));; 
		if(defaultColor == 14)
			defaultStack = new ItemStack(Items.BED);
			
		RecipeHandler.addOreDictRecipe(defaultStack,
				"WWW", "PPP",
				'W', new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE),
				'P', "plankWood");
	}

	public static int getColor(int colorIndex) {
		if(colorIndex >= 14)
			colorIndex = 15;

		return ItemDye.DYE_COLORS[15 -  colorIndex];
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}

