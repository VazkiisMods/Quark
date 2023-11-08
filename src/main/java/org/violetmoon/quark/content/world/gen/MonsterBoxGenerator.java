package org.violetmoon.quark.content.world.gen;

import org.violetmoon.quark.base.config.type.DimensionConfig;
import org.violetmoon.quark.base.world.generator.Generator;
import org.violetmoon.quark.content.world.module.MonsterBoxModule;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.material.Material;

public class MonsterBoxGenerator extends Generator {

	public MonsterBoxGenerator(DimensionConfig dimConfig) {
		super(dimConfig);
	}

	@Override
	public void generateChunk(WorldGenRegion world, ChunkGenerator generator, RandomSource rand, BlockPos chunkCorner) {
		if(generator instanceof FlatLevelSource)
			return;

		double chance = MonsterBoxModule.chancePerChunk;

		while(rand.nextDouble() <= chance) {
			BlockPos pos = chunkCorner.offset(rand.nextInt(16), MonsterBoxModule.minY + rand.nextInt(MonsterBoxModule.maxY - MonsterBoxModule.minY + 1), rand.nextInt(16));
			if(world.isEmptyBlock(pos)) {
				BlockPos testPos = pos;
				BlockState testState;
				int moves = 0;

				do {
					testPos = testPos.below();
					testState = world.getBlockState(testPos);
					moves++;
				} while(moves < MonsterBoxModule.searchRange && testState.getMaterial() != Material.STONE && testPos.getY() >= MonsterBoxModule.minY);

				if(testPos.getY() >= MonsterBoxModule.minY && world.isEmptyBlock(testPos.above()) && world.getBlockState(testPos).isFaceSturdy(world, testPos, Direction.UP)) {
					world.setBlock(testPos.above(), MonsterBoxModule.monster_box.defaultBlockState(), 0);
				}
			}

			chance -=MonsterBoxModule.chancePerChunk;
		}
	}

}
