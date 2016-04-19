/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 22:49:30 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.block.BlockModSlab;
import vazkii.quark.base.block.BlockModStairs;
import vazkii.quark.base.handler.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.BlockThatch;
import vazkii.quark.building.block.slab.BlockThatchSlab;
import vazkii.quark.building.block.stairs.BlockThatchStairs;

public class Thatch extends Feature {

	public static Block thatch;

	boolean enableStairsAndSlabs;

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true);
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
				'W', new ItemStack(Items.wheat));
		RecipeHandler.addShapelessOreDictRecipe(new ItemStack(Items.wheat, 4), new ItemStack(thatch));
	}

}
