package org.violetmoon.quark.base.world.generator.multichunk;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkGenerator;

import java.util.Random;
import java.util.function.BooleanSupplier;

import org.violetmoon.quark.base.config.type.ClusterSizeConfig;
import org.violetmoon.quark.base.config.type.DimensionConfig;
import org.violetmoon.quark.base.world.generator.Generator;

public abstract class ClusterBasedGenerator extends MultiChunkFeatureGenerator {

	public final ClusterShape.Provider shapeProvider;

	public ClusterBasedGenerator(DimensionConfig dimConfig, ClusterSizeConfig sizeConfig, long seedXor) {
		this(dimConfig, Generator.NO_COND, sizeConfig, seedXor);
	}

	public ClusterBasedGenerator(DimensionConfig dimConfig, BooleanSupplier condition, ClusterSizeConfig sizeConfig, long seedXor) {
		super(dimConfig, condition, seedXor);
		this.shapeProvider = new ClusterShape.Provider(sizeConfig, seedXor);
	}

	@Override
	public int getFeatureRadius() {
		return shapeProvider.getRadius();
	}

	@Override
	public void generateChunkPart(BlockPos src, ChunkGenerator generator, Random random, BlockPos chunkCorner, WorldGenRegion world) {
		final ClusterShape shape = shapeProvider.around(src);
		final IGenerationContext context = createContext(src, generator, random, chunkCorner, world);

		forEachChunkBlock(world, chunkCorner, shape.getLowerBound(), shape.getUpperBound(), (pos) -> {
			double noise = shape.noiseDiff(pos);
			if(noise > 0)
				context.consume(pos, noise);
		});

		if(context instanceof IFinishableContext finishableContext)
			finishableContext.finish();
	}

	public abstract IGenerationContext createContext(BlockPos src, ChunkGenerator generator, Random random, BlockPos chunkCorner, WorldGenRegion world);

	public interface IGenerationContext {
		void consume(BlockPos pos, double noise);
	}

	public interface IFinishableContext extends IGenerationContext {
		void finish();
	}

}
