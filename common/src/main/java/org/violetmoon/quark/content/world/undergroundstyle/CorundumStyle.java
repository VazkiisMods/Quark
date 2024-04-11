package org.violetmoon.quark.content.world.undergroundstyle;

import java.util.Random;

import org.violetmoon.quark.content.world.block.CorundumBlock;
import org.violetmoon.quark.content.world.block.CorundumClusterBlock;
import org.violetmoon.quark.content.world.module.CorundumModule;
import org.violetmoon.quark.content.world.undergroundstyle.base.BasicUndergroundStyle;
import org.violetmoon.quark.content.world.undergroundstyle.base.UndergroundStyleGenerator.Context;
import org.violetmoon.zeta.util.MiscUtil;

import it.unimi.dsi.fastutil.ints.Int2ByteArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class CorundumStyle extends BasicUndergroundStyle {

	public CorundumStyle() {
		super(Blocks.AIR.defaultBlockState(), Blocks.STONE.defaultBlockState(), Blocks.STONE.defaultBlockState());
	}

	private static final Int2ByteMap CRYSTAL_DATA = new Int2ByteArrayMap();

	@Override
	public void fillCeiling(Context context, BlockPos pos, BlockState state) {
		byte raw = calculateRawColorData(context.source);
		int floorIdx = raw & 0xF;
		int ceilIdx = (raw >> 4) & 0xF;
		if(ceilIdx >= floorIdx)
			ceilIdx++;

		if(context.random.nextDouble() < CorundumModule.crystalChance)
			makeCrystalIfApt(context, pos, Direction.DOWN, ceilIdx);
	}

	@Override
	public void fillFloor(Context context, BlockPos pos, BlockState state) {
		byte raw = calculateRawColorData(context.source);
		int floorIdx = raw & 0xF;

		if(context.random.nextDouble() < CorundumModule.crystalChance)
			makeCrystalIfApt(context, pos, Direction.UP, floorIdx);
	}

	private static void makeCrystalIfApt(Context context, BlockPos pos, Direction offset, int color) {
		BlockPos crystalPos = pos.relative(offset);
		boolean hasHorizontal = false;

		WorldGenRegion world = context.world;
		for(Direction dir : MiscUtil.HORIZONTALS) {
			BlockPos testPos = crystalPos.relative(dir);
			if(world.getBlockState(testPos).canOcclude()) {
				hasHorizontal = true;
				break;
			}
		}

		if(!hasHorizontal)
			return;

		makeCrystalAt(context, crystalPos, offset, color, CorundumModule.crystalClusterChance);

		if(context.random.nextDouble() < CorundumModule.doubleCrystalChance) {
			crystalPos = crystalPos.relative(offset);

			if(world.isEmptyBlock(crystalPos))
				makeCrystalAt(context, crystalPos, offset, color, 0);
		}
	}

	private static void makeCrystalAt(Context context, BlockPos crystalPos, Direction offset, int color, double clusterChance) {
		CorundumBlock crystal = CorundumModule.crystals.get(color);
		CorundumClusterBlock cluster = crystal.cluster;

		WorldGenRegion world = context.world;
		if(context.random.nextDouble() < clusterChance)
			world.setBlock(crystalPos, cluster.defaultBlockState().setValue(CorundumClusterBlock.FACING, offset).setValue(CorundumClusterBlock.WATERLOGGED, world.getFluidState(crystalPos).getType() == Fluids.WATER), 0);
		else {
			world.setBlock(crystalPos, crystal.defaultBlockState(), 0);

			for(Direction dir : Direction.values()) {
				BlockPos clusterPos = crystalPos.relative(dir);
				if(world.isEmptyBlock(clusterPos) && context.random.nextDouble() < CorundumModule.crystalClusterOnSidesChance)
					world.setBlock(clusterPos, cluster.defaultBlockState().setValue(CorundumClusterBlock.FACING, dir).setValue(CorundumClusterBlock.WATERLOGGED, world.getFluidState(clusterPos).getType() == Fluids.WATER), 0);
			}
		}
	}

	private static byte calculateRawColorData(BlockPos source) {
		return CRYSTAL_DATA.computeIfAbsent(source.hashCode(), (src) -> {
			Random rand = new Random(src);
			return (byte) ((rand.nextInt(8) << 4) | rand.nextInt(9));
		});
	}

}
