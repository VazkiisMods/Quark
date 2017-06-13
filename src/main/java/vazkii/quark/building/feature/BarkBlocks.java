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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockBark;
import vazkii.quark.building.block.slab.BlockBarkSlab;
import vazkii.quark.building.block.stairs.BlockBarkStairs;

public class BarkBlocks extends Feature {

	public static BlockMod bark;

	boolean enableWalls, enableStairsAndSlabs;

	@Override
	public void setupConfig() {
		enableWalls = loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		bark = new BlockBark();

		for(int i = 0; i < 6; i++) {
			ItemStack log = new ItemStack(i > 3 ? Blocks.LOG2 : Blocks.LOG, 1, i % 4);

			RecipeHandler.addOreDictRecipe(new ItemStack(bark, 4, i),
					"WW", "WW",
					'W', log);
			RecipeHandler.addShapelessOreDictRecipe(log, new ItemStack(bark, 1, i));
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
