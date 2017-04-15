/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [06/06/2016, 23:08:40 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.block.BlockNewSandstone;
import vazkii.quark.building.block.slab.BlockVanillaSlab;
import vazkii.quark.building.block.stairs.BlockVanillaStairs;

public class MoreSandstone extends Feature {

	public static BlockMod sandstone_new;

	boolean enableStairsAndSlabs;

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		sandstone_new = new BlockNewSandstone();
		
		RecipeHandler.addOreDictRecipe(new ItemStack(sandstone_new, 8, 0),
				"SSS", "S S", "SSS",
				'S', new ItemStack(Blocks.SANDSTONE));
		RecipeHandler.addOreDictRecipe(new ItemStack(sandstone_new, 4, 1),
				"SS", "SS",
				'S', new ItemStack(sandstone_new, 1, 0));
		RecipeHandler.addOreDictRecipe(new ItemStack(sandstone_new, 8, 2),
				"SSS", "S S", "SSS",
				'S', new ItemStack(Blocks.RED_SANDSTONE));
		RecipeHandler.addOreDictRecipe(new ItemStack(sandstone_new, 4, 3),
				"SS", "SS",
				'S', new ItemStack(sandstone_new, 1, 2));

		if(enableStairsAndSlabs) {
			boolean soulSandstone = ModuleLoader.isFeatureEnabled(SoulSandstone.class);
			
			for(BlockNewSandstone.Variants variant : BlockNewSandstone.Variants.class.getEnumConstants()) {
				if(!variant.stairs)
					continue;
				if(variant.ordinal() > 3 && !soulSandstone)
					break;

				IBlockState state = sandstone_new.getDefaultState().withProperty(sandstone_new.getVariantProp(), variant);
				String name = variant.getName() + "_stairs";
				BlockModStairs.initStairs(sandstone_new, variant.ordinal(), new BlockVanillaStairs(name, state));
			}

			for(BlockNewSandstone.Variants variant : BlockNewSandstone.Variants.class.getEnumConstants()) {
				if(!variant.slabs)
					continue;
				if(variant.ordinal() > 3 && !soulSandstone)
					break;

				IBlockState state = sandstone_new.getDefaultState().withProperty(sandstone_new.getVariantProp(), variant);
				String name = variant.getName() + "_slab";
				BlockModSlab.initSlab(sandstone_new, variant.ordinal(), new BlockVanillaSlab(name , state, false), new BlockVanillaSlab(name, state, true));
			}
		}
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		if(ModuleLoader.isFeatureEnabled(SoulSandstone.class)) {
			RecipeHandler.addOreDictRecipe(new ItemStack(sandstone_new, 8, 4),
					"SSS", "S S", "SSS",
					'S', new ItemStack(SoulSandstone.soul_sandstone));
			RecipeHandler.addOreDictRecipe(new ItemStack(sandstone_new, 4, 5),
					"SS", "SS",
					'S', new ItemStack(sandstone_new, 1, 4));
		}
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
