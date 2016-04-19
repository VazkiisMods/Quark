/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 22:44:08 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.block.BlockModSlab;
import vazkii.quark.base.block.BlockModStairs;
import vazkii.quark.base.handler.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.BlockSandyBricks;
import vazkii.quark.building.block.slab.BlockSandyBricksSlab;
import vazkii.quark.building.block.stairs.BlockSandyBricksStairs;

public class SandyBricks extends Feature {

	public static Block sandy_bricks;

	boolean enableStairsAndSlabs;
	boolean enableWalls;

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true);
		enableWalls = loadPropBool("Enable walls", "", true);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		sandy_bricks = new BlockSandyBricks();

		if(enableStairsAndSlabs) {
			BlockModStairs.initStairs(sandy_bricks, 0, new BlockSandyBricksStairs());
			BlockModSlab.initSlab(sandy_bricks, 0, new BlockSandyBricksSlab(false), new BlockSandyBricksSlab(true));
		}
		VanillaWalls.add("sandy_bricks", sandy_bricks, 0, enableWalls);

		RecipeHandler.addShapelessOreDictRecipe(new ItemStack(sandy_bricks), new ItemStack(Blocks.brick_block), new ItemStack(Blocks.sand));
	}

}
