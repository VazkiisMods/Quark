package org.violetmoon.quark.content.world.gen;

import org.violetmoon.quark.content.world.module.MonsterBoxModule;
import org.violetmoon.zeta.config.type.DimensionConfig;
import org.violetmoon.zeta.util.BlockUtils;
import org.violetmoon.zeta.world.generator.Generator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;

public class MonsterBoxGenerator extends Generator {

	public MonsterBoxGenerator(DimensionConfig dimConfig) {
		super(dimConfig);
	}

	@Override
	public void generateChunk(WorldGenRegion world, ChunkGenerator generator, RandomSource rand, BlockPos chunkCorner) {
		if(generator instanceof FlatLevelSource)
			return;

		double chance = MonsterBoxModule.chancePerChunk;
		while(chance > 0 && rand.nextDouble() <= chance) {
			BlockPos.MutableBlockPos pos = chunkCorner.offset(rand.nextInt(16), rand.nextInt(MonsterBoxModule.minY, MonsterBoxModule.maxY), rand.nextInt(16)).mutable();
			for(int moves = 0; moves < MonsterBoxModule.searchRange && pos.getY() > MonsterBoxModule.minY; moves++) {
				BlockState state = world.getBlockState(pos);
				if(canPlaceHere(world, pos, state)) {
					world.setBlock(pos, MonsterBoxModule.monster_box.defaultBlockState(), 0);
					break;
				}

				pos = pos.move(0, -1, 0); //down
			}

			chance -= 1;
		}
	}

	private boolean canPlaceHere(WorldGenRegion level, BlockPos.MutableBlockPos pos, BlockState state) {
		if(!state.canBeReplaced() || state.liquid())
			return false;

		//Mutable blockpos nudging dance to avoid an allocation, probably not worth it, stupidest thing ever, vibes based optimization
		BlockPos below = pos.move(0, -1, 0);
		BlockState belowState = level.getBlockState(below);
		boolean result = BlockUtils.isStoneBased(belowState, level, below) && belowState.isFaceSturdy(level, below, Direction.UP);

		pos.move(0, 1, 0);
		return result;
	}
}
