/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [30/06/2016, 14:52:57 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.BlockIronPlate;
import vazkii.quark.building.block.slab.BlockIronPlateSlab;
import vazkii.quark.building.block.stairs.BlockIronPlateStairs;

public class IronPlates extends Feature {

	public static Block iron_plate;

	boolean enableStairsAndSlabs;

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		iron_plate = new BlockIronPlate();

		if(enableStairsAndSlabs) {
			BlockModStairs.initStairs(iron_plate, 0, new BlockIronPlateStairs());
			BlockModSlab.initSlab(iron_plate, 0, new BlockIronPlateSlab(false), new BlockIronPlateSlab(true));
		}

		RecipeHandler.addOreDictRecipe(new ItemStack(iron_plate, 24),
				"III", "I I", "III",
				'I', "ingotIron");
		
		RecipeHandler.addOreDictRecipe(new ItemStack(iron_plate, 24, 1),
				"III", "IBI", "III",
				'I', "ingotIron",
				'B', new ItemStack(Items.WATER_BUCKET));
		
		RecipeHandler.addOreDictRecipe(new ItemStack(iron_plate, 8, 1),
				"III", "IBI", "III",
				'I', new ItemStack(iron_plate),
				'B', new ItemStack(Items.WATER_BUCKET));
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
