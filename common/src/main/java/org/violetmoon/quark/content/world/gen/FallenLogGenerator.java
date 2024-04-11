package org.violetmoon.quark.content.world.gen;

import java.util.ArrayList;
import java.util.List;

import org.violetmoon.quark.content.building.module.HollowLogsModule;
import org.violetmoon.quark.content.world.module.FallenLogsModule;
import org.violetmoon.zeta.config.type.DimensionConfig;
import org.violetmoon.zeta.util.MiscUtil;
import org.violetmoon.zeta.world.generator.Generator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public class FallenLogGenerator extends Generator {
	
	public FallenLogGenerator(DimensionConfig dimConfig) {
		super(dimConfig);
	}

	@Override
	public void generateChunk(WorldGenRegion worldIn, ChunkGenerator generator, RandomSource rand, BlockPos corner) {
		int x = corner.getX() + rand.nextInt(16);
		int z = corner.getZ() + rand.nextInt(16);
		BlockPos center = new BlockPos(x, 128, z);

		Holder<Biome> biome = getBiome(worldIn, center, false);

		int chance = biome.is(FallenLogsModule.reducedLogsTag) ? FallenLogsModule.sparseBiomeRarity : FallenLogsModule.rarity;
		if(rand.nextInt(chance) == 0) {
			BlockPos pos = worldIn.getHeightmapPos(Types.WORLD_SURFACE_WG, center);
			placeFallenLogAt(worldIn, pos);
		}
	}

	private static void placeFallenLogAt(LevelAccessor level, BlockPos pos) {
		placeFallenLogAt(level, pos, getLogBLockForPos(level, pos));
	}

	private static void placeFallenLogAt(LevelAccessor level, BlockPos pos, Block logBlock) {
		if(logBlock == Blocks.AIR)
			return;
		
		final int attempts = 5;
		
		BlockState state = logBlock.defaultBlockState();
		RandomSource rand = level.getRandom();

		for(int attempt = 0; attempt < attempts; attempt++) {
			int dirOrd = rand.nextInt(MiscUtil.HORIZONTALS.length);
			Direction dir = MiscUtil.HORIZONTALS[dirOrd];
			state = state.setValue(RotatedPillarBlock.AXIS, dir.getAxis());

			int len = 3 + rand.nextInt(2);
			
			boolean errored = false;
			
			for(int i = 0; i < len; i++) {
				BlockPos testPos = pos.relative(dir, i);
				BlockState testState = level.getBlockState(testPos);

				if(!testState.isAir() && !testState.canBeReplaced() && !testState.is(BlockTags.FLOWERS)) {
					errored = true;
					break;
				}

				BlockPos belowPos = testPos.below();
				BlockState belowState = level.getBlockState(belowPos);

				if(belowState.isAir()) {
					errored = true;
					break;
				}
			}
			
			if(errored)
				continue;

			for(int i = 0; i < len; i++) {
				BlockPos placePos = pos.relative(dir, i); 
				level.setBlock(placePos, state, 3);

				if(rand.nextInt(10) < 7) {
					BlockPos abovePos = placePos.above();
					BlockState aboveState = level.getBlockState(abovePos);
					if(aboveState.isAir()) {
						level.setBlock(abovePos, Blocks.MOSS_CARPET.defaultBlockState(), 3);
					}
				}

				final Direction[][] sideDirections = {
						{Direction.EAST, Direction.WEST},
						{Direction.EAST, Direction.WEST},
						{Direction.NORTH, Direction.SOUTH},
						{Direction.NORTH, Direction.SOUTH}
				};

				for(int j = 0; j < 2; j++)
					if(rand.nextInt(5) < 3) {
						Direction side = sideDirections[dirOrd][j];
						BlockPos sidePos = placePos.relative(side);
						placeDecorIfPossible(level, rand, side, sidePos);
					}
				
				if(rand.nextInt(10) < 4)
					placeDecorIfPossible(level, rand, dir, pos.relative(dir.getOpposite()));
				if(rand.nextInt(10) < 4)
					placeDecorIfPossible(level, rand, dir.getOpposite(), pos.relative(dir, len));
			}
			
			return;
		}
	}
	
	private static void placeDecorIfPossible(LevelAccessor level, RandomSource rand, Direction side, BlockPos sidePos) {
		BlockState sideState = level.getBlockState(sidePos);
		if(sideState.isAir()) {
			BlockState placeState = switch(rand.nextInt(3)) {
				case 0 -> Blocks.MOSS_CARPET.defaultBlockState();
				case 1 -> Blocks.VINE.defaultBlockState().setValue(VineBlock.getPropertyForFace(side.getOpposite()), true);
				default -> Blocks.FERN.defaultBlockState();
			};
			
			if(placeState.canSurvive(level, sidePos))
				level.setBlock(sidePos, placeState, 3);
		}
	}

	private static Block getLogBLockForPos(LevelAccessor level, BlockPos pos) {
		Block base = getBaseLogBlockForPos(level, pos);

		if(FallenLogsModule.useHollowLogs && HollowLogsModule.staticEnabled) {
			Block hollow = HollowLogsModule.logMap.get(base);
			if(hollow != null)
				return hollow;
		}

		return base;
	}

	private static Block getBaseLogBlockForPos(LevelAccessor level, BlockPos pos) {
		Holder<Biome> biome = level.getBiome(pos);
		List<Block> matched = new ArrayList<>();
		
		for(TagKey<Biome> tag : FallenLogsModule.blocksPerTag.keySet())
			if(biome.is(tag))
				matched.add(FallenLogsModule.blocksPerTag.get(tag));
		
		if(matched.size() == 0)
			return Blocks.AIR;
		
		return matched.get(level.getRandom().nextInt(matched.size()));
	}
	
}
