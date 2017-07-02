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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockGrassThatch;
import vazkii.quark.building.block.slab.BlockGrassThatchSlab;
import vazkii.quark.building.block.stairs.BlockGrassThatchStairs;

public class GrassThatch extends Feature {

	public static Block grass_thatch;

	boolean enableStairsAndSlabs;
	public static float fallDamageMultiplier;

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
		fallDamageMultiplier = (float) loadPropDouble("Fall damage multiplier", "", 0.5);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		grass_thatch = new BlockGrassThatch();

		if(enableStairsAndSlabs) {
			BlockModStairs.initStairs(grass_thatch, 0, new BlockGrassThatchStairs());
			BlockModSlab.initSlab(grass_thatch, 0, new BlockGrassThatchSlab(false), new BlockGrassThatchSlab(true));
		}

		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(grass_thatch),
				"TT", "TT",
				'T', ProxyRegistry.newStack(Items.TALLGRASS));
		RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(Items.TALLGRASS, 4), ProxyRegistry.newStack(grass_thatch));
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
