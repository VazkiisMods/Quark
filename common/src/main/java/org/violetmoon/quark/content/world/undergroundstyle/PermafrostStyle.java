package org.violetmoon.quark.content.world.undergroundstyle;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import org.violetmoon.quark.content.world.undergroundstyle.base.BasicUndergroundStyle;
import org.violetmoon.quark.content.world.undergroundstyle.base.UndergroundStyleGenerator.Context;

public class PermafrostStyle extends BasicUndergroundStyle {

	public PermafrostStyle() {
		super(Blocks.PACKED_ICE.defaultBlockState(), Blocks.PACKED_ICE.defaultBlockState(), Blocks.PACKED_ICE.defaultBlockState(), true);
	}

	public void setBlock(BlockState state) {
		ceilingState = floorState = state;
	}

	@Override
	public void fillFloor(Context context, BlockPos pos, BlockState state) {
		super.fillFloor(context, pos, state);

		LevelAccessor world = context.world;
		if(context.random.nextDouble() < 0.015) {
			int height = 3 + context.random.nextInt(3);
			for(int i = 0; i < height; i++) {
				pos = pos.above();

				if(world.getBlockState(pos).isAir())
					world.setBlock(pos, floorState, 2);
				else
					break;
			}
		}
	}
}
