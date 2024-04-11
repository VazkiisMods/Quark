package org.violetmoon.quark.content.world.gen;

import java.util.Random;

import org.violetmoon.quark.content.world.module.NewStoneTypesModule;
import org.violetmoon.quark.content.world.module.SpiralSpiresModule;
import org.violetmoon.zeta.config.type.DimensionConfig;
import org.violetmoon.zeta.util.BooleanSuppliers;
import org.violetmoon.zeta.world.generator.multichunk.MultiChunkFeatureGenerator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class SpiralSpireGenerator extends MultiChunkFeatureGenerator {

	public SpiralSpireGenerator(DimensionConfig dimConfig) {
		super(dimConfig, BooleanSuppliers.TRUE, 1892);
	}

	@Override
	public int getFeatureRadius() {
		return SpiralSpiresModule.radius;
	}

	@Override
	public void generateChunkPart(BlockPos src, ChunkGenerator generator, Random random, BlockPos chunkCorner, WorldGenRegion world) {
		double dist = (chunkCorner.distSqr(src)) / ((16 * SpiralSpiresModule.radius) * (16 * SpiralSpiresModule.radius));
		if(dist > 0.5 && random.nextDouble() < (1.5F - dist))
			return;

		BlockPos pos = chunkCorner.offset(random.nextInt(16), 256, random.nextInt(16));
		Holder<Biome> biome = getBiome(world, pos, false);
		if(!SpiralSpiresModule.biomes.canSpawn(biome))
			return;

		while(world.getBlockState(pos).getBlock() != Blocks.END_STONE) {
			pos = pos.below();

			if(pos.getY() < 10)
				return;
		}

		makeSpike(world, generator, random, pos);
	}

	@Override
	public BlockPos[] getSourcesInChunk(WorldGenRegion world, Random random, ChunkGenerator generator, BlockPos chunkCorner) {
		if(!chunkCorner.closerThan(Vec3i.ZERO, 1050) && SpiralSpiresModule.rarity > 0 && random.nextInt(SpiralSpiresModule.rarity) == 0)
			return new BlockPos[] { chunkCorner };

		return new BlockPos[0];
	}

	public void makeSpike(WorldGenRegion world, ChunkGenerator chunk, Random rand, BlockPos pos) {
		int height = 50 + rand.nextInt(20);
		double heightComposition = 5 + rand.nextDouble() * 1;
		int start = -5;
		int y = start;

		for(; y < height; y++) {
			BlockPos test = pos.above(y);
			BlockState state = world.getBlockState(test);
			if(!state.isAir() && !(state.getBlock() == Blocks.END_STONE || state.getBlock() == Blocks.CRYING_OBSIDIAN || state.getBlock() == Blocks.OBSIDIAN || state.getBlock() == SpiralSpiresModule.myalite_crystal))
				return;
		}
		y = start;

		for(; y < height; y++) {
			if(y < 0 && !world.getBlockState(pos.above(y)).canOcclude())
				continue;

			double r = Math.abs(((Math.PI / 2) - Math.atan((Math.max(0, y) + 0.5) / (height / heightComposition))) * 4);
			int ri = (int) Math.ceil(r);

			for(int i = -ri + 1; i < ri; i++)
				for(int j = -ri + 1; j < ri; j++)
					if((i * i + j * j) <= (ri * ri)) {
						boolean edge = i == (-ri + 1) || i == (ri - 1) || j == (-ri + 1) || j == (ri - 1);
						BlockState state = (edge && rand.nextFloat() < 0.2 ? NewStoneTypesModule.myaliteBlock : SpiralSpiresModule.dusky_myalite).defaultBlockState();
						world.setBlock(pos.offset(i, y, j), state, 2);
					}
		}

		int steps = 80 + rand.nextInt(30);
		int substeps = 10;

		int fullSteps = steps * substeps;
		int deteroirate = (int) ((0.5 + rand.nextDouble() * 0.3) * fullSteps);

		double spin = 0.12 + rand.nextDouble() * 0.16;
		double spread = 0.12 + rand.nextDouble() * 0.04;
		double upwardMotion = rand.nextDouble() * 0.2;

		if(rand.nextBoolean())
			spin *= -1;

		BlockState state = SpiralSpiresModule.myalite_crystal.defaultBlockState();

		for(int i = 0; i < fullSteps; i++) {
			double t = (double) i * spin;
			int x = (int) (Math.sin(t / substeps) * i * spread / substeps);
			int z = (int) (Math.cos(t / substeps) * i * spread / substeps);
			int yp = y + (int) Math.round(((double) i / substeps) * upwardMotion);

			BlockPos next = pos.offset(x, yp, z);

			float chance = 1F;
			if(i > deteroirate) {
				int deterStep = i - deteroirate;
				int maxSteps = (fullSteps - deteroirate);
				chance -= (float) deterStep / (float) maxSteps;
			}

			if(rand.nextFloat() < chance)
				world.setBlock(next, state, 2);
		}
	}

}
