package org.violetmoon.quark.base.world.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.WorldgenRandom;

import java.util.List;

/**
 * @author WireSegal
 * Created at 9:02 PM on 10/1/19.
 */
public class CombinedGenerator implements IGenerator {

	private List<? extends IGenerator> children;

	public CombinedGenerator(List<? extends IGenerator> children) {
		this.children = children;
	}

	@Override
	public int generate(int seedIncrement, long seed, GenerationStep.Decoration stage, WorldGenRegion worldIn, ChunkGenerator generator, WorldgenRandom rand, BlockPos pos) {
		for (IGenerator child : children) {
			if (child.canGenerate(worldIn))
				seedIncrement = child.generate(seedIncrement, seed, stage, worldIn, generator, rand, pos);
		}
		return seedIncrement;
	}

	@Override
	public boolean canGenerate(ServerLevelAccessor world) {
		return children.stream().anyMatch((it) -> it.canGenerate(world));
	}
}
