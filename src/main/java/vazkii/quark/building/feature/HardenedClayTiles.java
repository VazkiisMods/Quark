/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/03/2016, 01:24:16 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockHardenedClayTiles;
import vazkii.quark.building.block.BlockStainedClayTiles;
import vazkii.quark.building.block.slab.BlockHardenedClayTilesSlab;
import vazkii.quark.building.block.slab.BlockStainedClayTilesSlab;
import vazkii.quark.building.block.stairs.BlockHardenedClayTilesStairs;
import vazkii.quark.building.block.stairs.BlockStainedClayTilesStairs;

public class HardenedClayTiles extends Feature {

	public static BlockMod hardened_clay_tiles;
	public static BlockMod stained_clay_tiles;

	boolean enableStainedClay;
	boolean enableStairsAndSlabs;

	@Override
	public void setupConfig() {
		enableStainedClay = loadPropBool("Enable stained tiles", "", true) && GlobalConfig.enableVariants;
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		hardened_clay_tiles = new BlockHardenedClayTiles();

		if(enableStairsAndSlabs) {
			BlockModStairs.initStairs(hardened_clay_tiles, 0, new BlockHardenedClayTilesStairs());
			BlockModSlab.initSlab(hardened_clay_tiles, 0, new BlockHardenedClayTilesSlab(false), new BlockHardenedClayTilesSlab(true));
		}

		RecipeHandler.addOreDictRecipe(new ItemStack(hardened_clay_tiles, 4, 0),
				"BB", "BB",
				'B', new ItemStack(Blocks.HARDENED_CLAY));

		if(enableStainedClay) {
			stained_clay_tiles = new BlockStainedClayTiles();

			if(enableStairsAndSlabs) {
				for(BlockStainedClayTiles.Variants variant : BlockStainedClayTiles.Variants.class.getEnumConstants())
					BlockModStairs.initStairs(stained_clay_tiles, variant.ordinal(), new BlockStainedClayTilesStairs(variant));
				for(BlockStainedClayTiles.Variants variant : BlockStainedClayTiles.Variants.class.getEnumConstants())
					BlockModSlab.initSlab(stained_clay_tiles, variant.ordinal(), new BlockStainedClayTilesSlab(variant, false), new BlockStainedClayTilesSlab(variant, true));
			}

			for(int i = 0; i < 16; i++) {
				RecipeHandler.addOreDictRecipe(new ItemStack(stained_clay_tiles, 4, i),
						"BB", "BB",
						'B', new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, i));
				RecipeHandler.addOreDictRecipe(new ItemStack(stained_clay_tiles, 8, i),
						"BBB", "BDB", "BBB",
						'B', new ItemStack(hardened_clay_tiles, 1),
						'D', LibMisc.OREDICT_DYES.get(15 - i));
			}
		}
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
