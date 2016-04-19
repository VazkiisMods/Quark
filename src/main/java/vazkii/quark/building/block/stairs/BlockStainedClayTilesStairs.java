/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 18:32:01 (GMT)]
 */
package vazkii.quark.building.block.stairs;

import vazkii.quark.base.block.BlockModStairs;
import vazkii.quark.building.block.BlockStainedClayTiles;
import vazkii.quark.building.feature.HardenedClayTiles;

public class BlockStainedClayTilesStairs extends BlockModStairs {

	public BlockStainedClayTilesStairs(BlockStainedClayTiles.Variants variant) {
		super(variant.getName() + "_stairs", HardenedClayTiles.stained_clay_tiles.getDefaultState().withProperty(HardenedClayTiles.stained_clay_tiles.getVariantProp(), variant));
	}

}
