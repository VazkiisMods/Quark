/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/06/2016, 00:05:39 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.feature.RevampStoneGen;

public class ImprovedStoneToolCrafting extends Feature {

	@Override
	public void init(FMLInitializationEvent event) {
		String mat = "stoneToolMaterial";
		OreDictionary.registerOre(mat, new ItemStack(Items.FLINT));
		OreDictionary.registerOre(mat, new ItemStack(Blocks.STONE));
		OreDictionary.registerOre(mat, new ItemStack(Blocks.STONE, 1, 1));
		OreDictionary.registerOre(mat, new ItemStack(Blocks.STONE, 1, 3));
		OreDictionary.registerOre(mat, new ItemStack(Blocks.STONE, 1, 5));
		OreDictionary.registerOre(mat, new ItemStack(Blocks.COBBLESTONE));
		
		if(RevampStoneGen.enableLimestone)
			OreDictionary.registerOre(mat, new ItemStack(RevampStoneGen.limestone));
		if(RevampStoneGen.enableMarble)
			OreDictionary.registerOre(mat, new ItemStack(RevampStoneGen.marble));
		
		String[][] patterns = new String[][] {{"XXX", " # ", " # "}, {"X", "#", "#"}, {"XX", "X#", " #"}, {"XX", " #", " #"}, {"X", "X", "#"}};
		Item[] items = new Item[] { Items.STONE_PICKAXE, Items.STONE_SHOVEL, Items.STONE_AXE, Items.STONE_HOE, Items.STONE_SWORD };

		for(int i = 0; i < patterns.length; i++)
			RecipeHandler.addOreDictRecipe(new ItemStack(items[i]),
					patterns[i][0], patterns[i][1], patterns[i][2],
					'X', mat,
					'#', new ItemStack(Items.STICK));
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
