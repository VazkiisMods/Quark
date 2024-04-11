package org.violetmoon.quark.content.world.undergroundstyle.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.violetmoon.quark.content.world.undergroundstyle.base.UndergroundStyleGenerator.Context;

public class BasicUndergroundStyle extends UndergroundStyle {

	public BlockState floorState, ceilingState, wallState;
	public final boolean mimicInside;

	public BasicUndergroundStyle(BlockState floorState, BlockState ceilingState, BlockState wallState) {
		this(floorState, ceilingState, wallState, false);
	}

	public BasicUndergroundStyle(BlockState floorState, BlockState ceilingState, BlockState wallState, boolean mimicInside) {
		this.floorState = floorState;
		this.ceilingState = ceilingState;
		this.wallState = wallState;
		this.mimicInside = mimicInside;
	}

	@Override
	public void fillFloor(Context context, BlockPos pos, BlockState state) {
		if(floorState != null)
			context.world.setBlock(pos, floorState, 2);
	}

	@Override
	public void fillCeiling(Context context, BlockPos pos, BlockState state) {
		if(ceilingState != null)
			context.world.setBlock(pos, ceilingState, 2);
	}

	@Override
	public void fillWall(Context context, BlockPos pos, BlockState state) {
		if(wallState != null)
			context.world.setBlock(pos, wallState, 2);
	}

	@Override
	public void fillInside(Context context, BlockPos pos, BlockState state) {
		if(mimicInside)
			fillWall(context, pos, state);
	}

}
