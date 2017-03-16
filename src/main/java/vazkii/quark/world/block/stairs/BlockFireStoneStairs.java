package vazkii.quark.world.block.stairs;

import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.world.feature.RevampStoneGen;
import vazkii.quark.world.feature.UndergroundBiomes;

public class BlockFireStoneStairs extends BlockQuarkStairs {

	public BlockFireStoneStairs() {
		super("fire_stone_stairs", UndergroundBiomes.biome_cobblestone.getStateFromMeta(0));
	}
	
}