package vazkii.quark.building.block.stairs;

import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.building.block.BlockBark;
import vazkii.quark.building.feature.BarkBlocks;

public class BlockBarkStairs extends BlockQuarkStairs {

	public BlockBarkStairs(BlockBark.Variants variant) {
		super(variant.getName() + "_stairs", BarkBlocks.bark.getDefaultState().withProperty(BarkBlocks.bark.getVariantProp(), variant));
	}

}
