package org.violetmoon.quark.content.world.gen;

import java.util.Random;

import org.violetmoon.quark.content.world.module.ChorusVegetationModule;
import org.violetmoon.zeta.config.type.DimensionConfig;
import org.violetmoon.zeta.util.BooleanSuppliers;
import org.violetmoon.zeta.world.generator.multichunk.MultiChunkFeatureGenerator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class ChorusVegetationGenerator extends MultiChunkFeatureGenerator {

	public ChorusVegetationGenerator() {
		super(DimensionConfig.end(false), BooleanSuppliers.TRUE, 2093);
	}

	@Override
	public int getFeatureRadius() {
		return ChorusVegetationModule.radius;
	}

	@Override
	public BlockPos[] getSourcesInChunk(WorldGenRegion world, Random random, ChunkGenerator generator, BlockPos chunkCorner) {
		if(!chunkCorner.closerThan(Vec3i.ZERO, 1050) && ChorusVegetationModule.rarity > 0 && random.nextInt(ChorusVegetationModule.rarity) == 0) {
			Holder<Biome> b = getBiome(world, chunkCorner, true);
			if(b.is(Biomes.END_HIGHLANDS.location()))
				return new BlockPos[] { chunkCorner };
		}

		return new BlockPos[0];
	}

	@Override
	public void generateChunkPart(BlockPos src, ChunkGenerator generator, Random rand, BlockPos pos, WorldGenRegion worldIn) {
		for(int i = 0; i < ChorusVegetationModule.chunkAttempts; i++) {
			BlockPos placePos = pos.offset(rand.nextInt(16), 100, rand.nextInt(16));

			Holder<Biome> b = getBiome(worldIn, placePos, true);
			double chance = getChance(b);

			double dist = ((placePos.getX() - src.getX()) * (placePos.getX() - src.getX())) + ((placePos.getZ() - src.getZ()) * (placePos.getZ() - src.getZ()));
			int ditherStart = 6;

			ditherStart *= ditherStart;
			if(dist > ditherStart)
				chance *= (1 - (Math.atan((dist - ditherStart) / 50) / (Math.PI / 2)));

			if(chance > 0 && rand.nextDouble() < chance) {
				while(placePos.getY() > 40) {
					BlockState stateAt = worldIn.getBlockState(placePos);
					if(stateAt.getBlock() == Blocks.END_STONE)
						break;

					placePos = placePos.below();
				}

				if(worldIn.getBlockState(placePos).getBlock() == Blocks.END_STONE && worldIn.getBlockState(placePos.above()).isAir()) {
					Block block = (rand.nextDouble() < 0.1) ? ChorusVegetationModule.chorus_twist : ChorusVegetationModule.chorus_weeds;
					worldIn.setBlock(placePos.above(), block.defaultBlockState(), 2);
				}
			}
		}
	}

	private double getChance(Holder<Biome> b) {
		if(b.is(Biomes.END_HIGHLANDS.location()))
			return ChorusVegetationModule.highlandsChance;
		else if(b.is(Biomes.END_MIDLANDS.location()))
			return ChorusVegetationModule.midlandsChance;
		else
			return ChorusVegetationModule.otherEndBiomesChance;
	}

}
