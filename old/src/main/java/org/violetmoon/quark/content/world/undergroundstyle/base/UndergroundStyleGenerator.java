package org.violetmoon.quark.content.world.undergroundstyle.base;

import java.util.Random;

import org.violetmoon.zeta.world.generator.multichunk.ClusterBasedGenerator;
import org.violetmoon.zeta.world.generator.multichunk.ClusterBasedGenerator.IGenerationContext;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;

public class UndergroundStyleGenerator extends ClusterBasedGenerator {

	public final UndergroundStyleConfig info;

	public UndergroundStyleGenerator(UndergroundStyleConfig info, String name) {
		super(info.dimensions, info, name.hashCode());
		this.info = info;
	}

	@Override
	public int getFeatureRadius() {
		return info.horizontalSize + info.horizontalVariation;
	}

	@Override
	public BlockPos[] getSourcesInChunk(WorldGenRegion world, Random random, ChunkGenerator generator, BlockPos chunkCorner) {
		if(info.rarity > 0 && random.nextInt(info.rarity) == 0) {
			int x = chunkCorner.getX() + random.nextInt(16);
			int y = random.nextInt(info.minYLevel, info.maxYLevel);
			int z = chunkCorner.getZ() + random.nextInt(16);
			BlockPos pos = new BlockPos(x, y, z);

			//check the biome at world height, and don't start blobs unless theyre actually underground
			if(info.biomes.canSpawn(getBiome(world, pos, true)) && world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) >= y)
				return new BlockPos[]{ pos };
		}

		return new BlockPos[0];
	}

	@Override
	public IGenerationContext createContext(BlockPos src, ChunkGenerator generator, Random random, BlockPos chunkCorner, WorldGenRegion world) {
		return new Context(world, src, generator, random, info);
	}

	@Override
	public String toString() {
		return "UndergroundBiomeGenerator[" + info.style + "]";
	}

	public static class Context implements IGenerationContext {

		public final WorldGenRegion world;
		public final BlockPos source;
		public final ChunkGenerator generator;
		public final Random random;
		public final UndergroundStyleConfig info;

		public Context(WorldGenRegion world, BlockPos source, ChunkGenerator generator, Random random, UndergroundStyleConfig info) {
			this.world = world;
			this.source = source;
			this.generator = generator;
			this.random = random;
			this.info = info;
		}

		@Override
		public boolean canPlaceAt(BlockPos pos) {
			return world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ()) > pos.getY();
		}

		@Override
		public void consume(BlockPos pos) {
			info.style.fill(this, pos);
		}

	}
}
