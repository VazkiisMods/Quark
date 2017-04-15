/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/06/2016, 12:05:49 (GMT)]
 */
package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockCharcoal;

public class CharcoalBlock extends Feature {

	public static Block charcoal_block;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		charcoal_block = new BlockCharcoal();

		RecipeHandler.addOreDictRecipe(new ItemStack(charcoal_block),
				"CCC", "CCC", "CCC",
				'C', new ItemStack(Items.COAL, 1, 1));
		RecipeHandler.addShapelessOreDictRecipe(new ItemStack(Items.COAL, 9, 1), new ItemStack(charcoal_block));

		OreDictionary.registerOre("blockCharcoal", charcoal_block);
		
		GameRegistry.registerFuelHandler(new IFuelHandler() {

			@Override
			public int getBurnTime(ItemStack stack) {
				return !stack.isEmpty() && stack.getItem() == Item.getItemFromBlock(charcoal_block) ? 16000 : 0;
			}

		});
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "actuallyadditions" };
	}
	
}
