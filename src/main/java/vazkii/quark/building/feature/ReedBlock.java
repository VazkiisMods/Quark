/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 23:37:35 (GMT)]
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
import vazkii.quark.building.block.BlockReed;
import vazkii.quark.building.block.slab.BlockReedSlab;
import vazkii.quark.building.block.stairs.BlockReedStairs;

public class ReedBlock extends Feature {

	public static Block reed_block;

	boolean enableStairsAndSlabs;
	boolean enableWalls;

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
		enableWalls = loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		reed_block = new BlockReed();

		if(enableStairsAndSlabs) {
			BlockModStairs.initStairs(reed_block, 0, new BlockReedStairs());
			BlockModSlab.initSlab(reed_block, 0, new BlockReedSlab(false), new BlockReedSlab(true));
		}
		VanillaWalls.add("reed_block", reed_block, 0, enableWalls);

		RecipeHandler.addOreDictRecipe(new ItemStack(reed_block),
				"RRR", "RRR", "RRR",
				'R', new ItemStack(Items.REEDS));
		RecipeHandler.addShapelessOreDictRecipe(new ItemStack(Items.REEDS, 9), new ItemStack(reed_block));
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
