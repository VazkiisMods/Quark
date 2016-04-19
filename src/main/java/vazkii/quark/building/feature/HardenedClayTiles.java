/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [19/03/2016, 01:24:16 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.block.BlockMod;
import vazkii.quark.base.block.BlockModSlab;
import vazkii.quark.base.block.BlockModStairs;
import vazkii.quark.base.handler.RecipeHandler;
import vazkii.quark.base.module.Feature;
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
		enableStainedClay = loadPropBool("Enable stained tiles", "", true);
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true);
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
				'B', new ItemStack(Blocks.hardened_clay));

		if(enableStainedClay) {
			stained_clay_tiles = new BlockStainedClayTiles();

			if(enableStairsAndSlabs) {
				for(BlockStainedClayTiles.Variants variant : BlockStainedClayTiles.Variants.class.getEnumConstants())
					BlockModStairs.initStairs(stained_clay_tiles, variant.ordinal(), new BlockStainedClayTilesStairs(variant));
				for(BlockStainedClayTiles.Variants variant : BlockStainedClayTiles.Variants.class.getEnumConstants())
					BlockModSlab.initSlab(stained_clay_tiles, variant.ordinal(), new BlockStainedClayTilesSlab(variant, false), new BlockStainedClayTilesSlab(variant, true));
			}

			for(int i = 0; i < 16; i++)
				RecipeHandler.addOreDictRecipe(new ItemStack(stained_clay_tiles, 4, i),
						"BB", "BB",
						'B', new ItemStack(Blocks.stained_hardened_clay, 1, i));
		}
	}

}
