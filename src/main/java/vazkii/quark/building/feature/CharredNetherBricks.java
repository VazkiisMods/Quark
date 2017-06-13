/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [06/06/2016, 21:20:45 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockCharredNetherBricks;
import vazkii.quark.building.block.slab.BlockCharredNetherBrickSlab;
import vazkii.quark.building.block.stairs.BlockCharredNetherBrickStairs;

public class CharredNetherBricks extends Feature {

	public static Block charred_nether_bricks;

	boolean enableStairsAndSlabs;

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		charred_nether_bricks = new BlockCharredNetherBricks();

		RecipeHandler.addShapelessOreDictRecipe(new ItemStack(charred_nether_bricks, 3), new ItemStack(Blocks.NETHER_BRICK), new ItemStack(Blocks.NETHER_BRICK), new ItemStack(Blocks.NETHER_BRICK), new ItemStack(Items.FIRE_CHARGE));

		if(enableStairsAndSlabs) {
			BlockModStairs.initStairs(charred_nether_bricks, 0, new BlockCharredNetherBrickStairs());
			BlockModSlab.initSlab(charred_nether_bricks, 0, new BlockCharredNetherBrickSlab(false), new BlockCharredNetherBrickSlab(true));
		}
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
