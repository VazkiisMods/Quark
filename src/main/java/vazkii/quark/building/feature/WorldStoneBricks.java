/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 19:56:42 (GMT)]
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
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockWorldStoneBricks;
import vazkii.quark.building.block.slab.BlockVanillaSlab;
import vazkii.quark.building.block.stairs.BlockVanillaStairs;
import vazkii.quark.world.feature.Basalt;
import vazkii.quark.world.feature.RevampStoneGen;

public class WorldStoneBricks extends Feature {

	public static BlockMod world_stone_bricks;

	boolean enableStairsAndSlabs;
	boolean enableWalls;

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
		enableWalls = loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		world_stone_bricks = new BlockWorldStoneBricks();

		if(enableStairsAndSlabs) {
			for(BlockWorldStoneBricks.Variants variant : BlockWorldStoneBricks.Variants.class.getEnumConstants()) {
				if(!variant.isEnabled())
					continue;

				IBlockState state = world_stone_bricks.getDefaultState().withProperty(world_stone_bricks.getVariantProp(), variant);
				String name = variant.getName() + "_stairs";
				BlockModStairs.initStairs(world_stone_bricks, variant.ordinal(), new BlockVanillaStairs(name, state));
			}

			for(BlockWorldStoneBricks.Variants variant : BlockWorldStoneBricks.Variants.class.getEnumConstants()) {
				if(!variant.isEnabled())
					continue;

				IBlockState state = world_stone_bricks.getDefaultState().withProperty(world_stone_bricks.getVariantProp(), variant);
				String name = variant.getName() + "_slab";
				BlockModSlab.initSlab(world_stone_bricks, variant.ordinal(), new BlockVanillaSlab(name , state, false), new BlockVanillaSlab(name, state, true));
			}
		}

		for(BlockWorldStoneBricks.Variants variant : BlockWorldStoneBricks.Variants.class.getEnumConstants()) {
			if(!variant.isEnabled())
				continue;

			world_stone_bricks.getDefaultState().withProperty(world_stone_bricks.getVariantProp(), variant);
			String name = variant.getName();
			VanillaWalls.add(name, world_stone_bricks, variant.ordinal(), enableWalls);
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		for(int i = 0; i < 3; i++)
			RecipeHandler.addOreDictRecipe(new ItemStack(world_stone_bricks, 4, i),
					"SS", "SS",
					'S', new ItemStack(Blocks.STONE, 1, i * 2 + 2));

		if(BlockWorldStoneBricks.Variants.STONE_BASALT_BRICKS.isEnabled())
			RecipeHandler.addOreDictRecipe(new ItemStack(world_stone_bricks, 4, 3),
					"SS", "SS",
					'S', new ItemStack(Basalt.basalt, 1, 1));
		
		if(BlockWorldStoneBricks.Variants.STONE_MARBLE_BRICKS.isEnabled())
			RecipeHandler.addOreDictRecipe(new ItemStack(world_stone_bricks, 4, 4),
					"SS", "SS",
					'S', new ItemStack(RevampStoneGen.marble, 1, 1));
		
		if(BlockWorldStoneBricks.Variants.STONE_LIMESTONE_BRICKS.isEnabled())
			RecipeHandler.addOreDictRecipe(new ItemStack(world_stone_bricks, 4, 5),
					"SS", "SS",
					'S', new ItemStack(RevampStoneGen.limestone, 1, 1));
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
