package org.violetmoon.quark.content.world.undergroundstyle.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.violetmoon.quark.base.world.generator.multichunk.ClusterBasedGenerator;

import java.util.*;

public class UndergroundStyleGenerator<T extends UndergroundStyle> extends ClusterBasedGenerator {

	public final UndergroundStyleConfig<T> info;

	public UndergroundStyleGenerator(UndergroundStyleConfig<T> info, String name) {
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
			return new BlockPos[] {
					chunkCorner.offset(random.nextInt(16), info.minYLevel + random.nextInt(info.maxYLevel - info.minYLevel), random.nextInt(16))
			};
		}

		return new BlockPos[0];
	}
	
	@Override
	public IGenerationContext createContext(BlockPos src, ChunkGenerator generator, Random random, BlockPos chunkCorner, WorldGenRegion world) {
		return new Context(world, src, generator, random, info);
	}

	@Override
	public boolean isSourceValid(WorldGenRegion world, ChunkGenerator generator, BlockPos pos) {
		BlockPos check = new BlockPos(pos.getX(), info.minYLevel, pos.getZ());
		Holder<Biome> biome = getBiome(world, check, true);
		return info.biomes.canSpawn(biome);
	}
	
	@Override
	public String toString() {
		return "UndergroundBiomeGenerator[" + info.biomeObj + "]";
	}

	public static class Context implements IFinishableContext {

		public final WorldGenRegion world;
		public final BlockPos source;
		public final ChunkGenerator generator;
		public final Random random;
		public final UndergroundStyleConfig<?> info;

		public final List<BlockPos> floorList = new LinkedList<>();
		public final List<BlockPos> ceilingList = new LinkedList<>();
		public final List<BlockPos> insideList = new LinkedList<>();

		public final Map<BlockPos, Direction> wallMap = new HashMap<>();
		
		public Context(WorldGenRegion world, BlockPos source, ChunkGenerator generator, Random random, UndergroundStyleConfig<?> info) {
			this.world = world;
			this.source = source;
			this.generator = generator;
			this.random = random;
			this.info = info;
		}

		@Override
		public void consume(BlockPos pos, double noise) {
			info.biomeObj.fill(this, pos);			
		}

		@Override
		public void finish() {
			floorList.forEach(pos -> info.biomeObj.finalFloorPass(this, pos));
			ceilingList.forEach(pos -> info.biomeObj.finalCeilingPass(this, pos));
			wallMap.keySet().forEach(pos -> info.biomeObj.finalWallPass(this, pos));
			insideList.forEach(pos -> info.biomeObj.finalInsidePass(this, pos));			
		}
		
	}
}
