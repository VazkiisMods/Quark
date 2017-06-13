/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 22:49:30 (GMT)]
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
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockThatch;
import vazkii.quark.building.block.slab.BlockThatchSlab;
import vazkii.quark.building.block.stairs.BlockThatchStairs;

public class Thatch extends Feature {

	public static Block thatch;

	boolean enableStairsAndSlabs;
	public static float fallDamageMultiplier;

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
		fallDamageMultiplier = (float) loadPropDouble("Fall damage multiplier", "", 0.5);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		thatch = new BlockThatch();

		if(enableStairsAndSlabs) {
			BlockModStairs.initStairs(thatch, 0, new BlockThatchStairs());
			BlockModSlab.initSlab(thatch, 0, new BlockThatchSlab(false), new BlockThatchSlab(true));
		}

		RecipeHandler.addOreDictRecipe(new ItemStack(thatch),
				"WW", "WW",
				'W', new ItemStack(Items.WHEAT));
		RecipeHandler.addShapelessOreDictRecipe(new ItemStack(Items.WHEAT, 4), new ItemStack(thatch));
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
