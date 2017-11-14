/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [24/03/2016, 15:24:42 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockBark;
import vazkii.quark.building.block.slab.BlockBarkSlab;
import vazkii.quark.building.block.stairs.BlockBarkStairs;

public class BarkBlocks extends Feature {

	public static BlockMod bark;

	boolean enableWalls, enableStairsAndSlabs, use3x3;

	@Override
	public void setupConfig() {
		enableWalls = loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
		use3x3 = loadPropBool("Use 3x3 Recipe", "Set this to true to use a 3x3 recipe instead of 2x2.\nTurn this on if you have the Forestry mod to prevent recipe overlap.", false);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		bark = new BlockBark();

		for(int i = 0; i < 6; i++) {
			ItemStack log = ProxyRegistry.newStack(i > 3 ? Blocks.LOG2 : Blocks.LOG, 1, i % 4);

			if(use3x3)
				RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(bark, 9, i),
						"WWW", "WWW", "WWW",
						'W', log);
			else RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(bark, 4, i),
					"WW", "WW",
					'W', log);
			RecipeHandler.addShapelessOreDictRecipe(log, ProxyRegistry.newStack(bark, 1, i));
		}

		for(BlockBark.Variants variant : BlockBark.Variants.class.getEnumConstants()) {
			bark.getDefaultState().withProperty(bark.getVariantProp(), variant);
			String name = variant.getName();
			VanillaWalls.add(name, bark, variant.ordinal(), enableWalls);
			
			if(enableStairsAndSlabs) {
				BlockModStairs.initStairs(bark, variant.ordinal(), new BlockBarkStairs(variant));
				BlockModSlab.initSlab(bark, variant.ordinal(), new BlockBarkSlab(variant, false), new BlockBarkSlab(variant, true));
			}
		}
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
