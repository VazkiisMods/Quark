/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [18/04/2016, 22:43:48 (GMT)]
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
import vazkii.quark.building.block.BlockSnowBricks;
import vazkii.quark.building.block.slab.BlockSnowBricksSlab;
import vazkii.quark.building.block.stairs.BlockSnowBricksStairs;

public class SnowBricks extends Feature {

	public static Block snow_bricks;
	
	boolean enableStairsAndSlabs;
	boolean enableWalls;

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true);
		enableWalls = loadPropBool("Enable walls", "", true);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		snow_bricks = new BlockSnowBricks();
		
		if(enableStairsAndSlabs) {
			BlockModStairs.initStairs(snow_bricks, 0, new BlockSnowBricksStairs());
			BlockModSlab.initSlab(snow_bricks, 0, new BlockSnowBricksSlab(false), new BlockSnowBricksSlab(true));
		}
		VanillaWalls.add("snow_bricks", snow_bricks, 0, enableWalls);
		
		RecipeHandler.addOreDictRecipe(new ItemStack(snow_bricks, 4), 
				"SS", "SS",
				'S', new ItemStack(Blocks.snow));
	}
	
}
