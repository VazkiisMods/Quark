package org.violetmoon.quark.base.world.generator;

import java.util.BitSet;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import org.violetmoon.quark.base.config.type.DimensionConfig;
import org.violetmoon.quark.base.config.type.OrePocketConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;

public class OreGenerator extends Generator {

	public static final Predicate<BlockState> STONE_MATCHER = (state) -> {
		if(state == null)
			return false;

		Block block = state.getBlock();
		return block == Blocks.STONE || block == Blocks.DEEPSLATE;
	};

	public static final Predicate<BlockState> NETHERRACK_MATCHER = (state) -> {
		if(state == null)
			return false;

		Block block = state.getBlock();
		return block == Blocks.NETHERRACK;
	};

	public static final Predicate<BlockState> ENDSTONE_MATCHER = (state) -> {
		if(state == null)
			return false;

		Block block = state.getBlock();
		return block == Blocks.END_STONE;
	};

	public static final Predicate<BlockState> ALL_DIMS_STONE_MATCHER = STONE_MATCHER.or(NETHERRACK_MATCHER).or(ENDSTONE_MATCHER);

	private final OrePocketConfig oreConfig;
	private final BlockState placeState;
	private final Predicate<BlockState> matcher;

	public OreGenerator(DimensionConfig dimConfig, OrePocketConfig oreConfig, BlockState placeState, Predicate<BlockState> matcher, BooleanSupplier condition) {
		super(dimConfig, condition);
		this.oreConfig = oreConfig;
		this.placeState = placeState;
		this.matcher = matcher;
	}

	@Override
	public void generateChunk(WorldGenRegion worldIn, ChunkGenerator generator, RandomSource rand, BlockPos pos) {
		oreConfig.forEach(pos, rand, npos -> place(worldIn, rand, npos));
	}

	// =============================================================================================
	// ALL VANILLA COPY PASTE FROM HERE ON OUT
	// VENTURE ONLY IF YOU'RE BRAVER THAN ME
	// =============================================================================================

	public boolean place(LevelAccessor worldIn, RandomSource rand, BlockPos pos) {
		float angle = rand.nextFloat() * (float)Math.PI;
		float factor = (float)oreConfig.clusterSize / 8.0F;
		int minFactor = Mth.ceil(((float)oreConfig.clusterSize / 16.0F * 2.0F + 1.0F) / 2.0F);
		double x1 = (float)pos.getX() + Mth.sin(angle) * factor;
		double x2 = (float)pos.getX() - Mth.sin(angle) * factor;
		double z1 = (float)pos.getZ() + Mth.cos(angle) * factor;
		double z2 = (float)pos.getZ() - Mth.cos(angle) * factor;
		double y1 = pos.getY() + rand.nextInt(3) - 2;
		double y2 = pos.getY() + rand.nextInt(3) - 2;
		int maxX = pos.getX() - Mth.ceil(factor) - minFactor;
		int maxY = pos.getY() - 2 - minFactor;
		int maxZ = pos.getZ() - Mth.ceil(factor) - minFactor;
		int searchSize = 2 * (Mth.ceil(factor) + minFactor);
		int secondarySearchSize = 2 * (2 + minFactor);

		Heightmap.Types hm = worldIn instanceof WorldGenRegion ? Heightmap.Types.OCEAN_FLOOR_WG : Heightmap.Types.WORLD_SURFACE;

		for(int x = maxX; x <= maxX + searchSize; ++x) {
			for(int z = maxZ; z <= maxZ + searchSize; ++z) {
				if (maxY <= worldIn.getHeight(hm, x, z)) {
					return this.doPlace(worldIn, rand, x1, x2, z1, z2, y1, y2, maxX, maxY, maxZ, searchSize, secondarySearchSize);
				}
			}
		}

		return false;
	}

	protected boolean doPlace(LevelAccessor worldIn, RandomSource random, double x1, double x2, double z1, double z2, double y1, double y2, int maxX, int maxY, int maxZ, int searchSize, int secondarySearchSize) {
		int blocksPlaced = 0;
		BitSet bitset = new BitSet(searchSize * secondarySearchSize * searchSize);
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		double[] clusterInfo = new double[oreConfig.clusterSize * 4];

		for(int clusterSize = 0; clusterSize < oreConfig.clusterSize; ++clusterSize) {
			float clusterSizeRatio = (float)clusterSize / (float)oreConfig.clusterSize;
			double x = Mth.lerp(clusterSizeRatio, x1, x2);
			double y = Mth.lerp(clusterSizeRatio, y1, y2);
			double z = Mth.lerp(clusterSizeRatio, z1, z2);
			double randSize = random.nextDouble() * (double)oreConfig.clusterSize / 16.0D;
			double size = ((double)(Mth.sin((float)Math.PI * clusterSizeRatio) + 1.0F) * randSize + 1.0D) / 2.0D;
			clusterInfo[clusterSize * 4] = x;
			clusterInfo[clusterSize * 4 + 1] = y;
			clusterInfo[clusterSize * 4 + 2] = z;
			clusterInfo[clusterSize * 4 + 3] = size;
		}

		for(int size1 = 0; size1 < oreConfig.clusterSize - 1; ++size1) {
			if (!(clusterInfo[size1 * 4 + 3] <= 0.0D)) {
				for(int size2 = size1 + 1; size2 < oreConfig.clusterSize; ++size2) {
					if (!(clusterInfo[size2 * 4 + 3] <= 0.0D)) {
						double dX = clusterInfo[size1 * 4] - clusterInfo[size2 * 4];
						double dY = clusterInfo[size1 * 4 + 1] - clusterInfo[size2 * 4 + 1];
						double dZ = clusterInfo[size1 * 4 + 2] - clusterInfo[size2 * 4 + 2];
						double dSize = clusterInfo[size1 * 4 + 3] - clusterInfo[size2 * 4 + 3];
						if (dSize * dSize > dX * dX + dY * dY + dZ * dZ) {
							if (dSize > 0.0D) {
								clusterInfo[size2 * 4 + 3] = -1.0D;
							} else {
								clusterInfo[size1 * 4 + 3] = -1.0D;
							}
						}
					}
				}
			}
		}

		for(int clusterSize = 0; clusterSize < oreConfig.clusterSize; ++clusterSize) {
			double size = clusterInfo[clusterSize * 4 + 3];
			if (size >= 0) {
				double x = clusterInfo[clusterSize * 4];
				double y = clusterInfo[clusterSize * 4 + 1];
				double z = clusterInfo[clusterSize * 4 + 2];
				int clusterMinX = Math.max(Mth.floor(x - size), maxX);
				int clusterMinY = Math.max(Mth.floor(y - size), maxY);
				int clusterMinZ = Math.max(Mth.floor(z - size), maxZ);
				int clusterMaxX = Math.max(Mth.floor(x + size), clusterMinX);
				int clusterMaxY = Math.max(Mth.floor(y + size), clusterMinY);
				int clusterMaxZ = Math.max(Mth.floor(z + size), clusterMinZ);

				for(int clusterX = clusterMinX; clusterX <= clusterMaxX; ++clusterX) {
					double xSize = ((double)clusterX + 0.5D - x) / size;
					if (xSize * xSize < 1.0D) {
						for(int clusterY = clusterMinY; clusterY <= clusterMaxY; ++clusterY) {
							double ySize = ((double)clusterY + 0.5D - y) / size;
							if (xSize * xSize + ySize * ySize < 1.0D) {
								for(int clusterZ = clusterMinZ; clusterZ <= clusterMaxZ; ++clusterZ) {
									double zSize = ((double)clusterZ + 0.5D - z) / size;
									if (xSize * xSize + ySize * ySize + zSize * zSize < 1.0D) {
										int index = clusterX - maxX + (clusterY - maxY) * searchSize + (clusterZ - maxZ) * searchSize * secondarySearchSize;
										if (!bitset.get(index)) {
											bitset.set(index);
											pos.set(clusterX, clusterY, clusterZ);
											if (matcher.test(worldIn.getBlockState(pos))) {
												worldIn.setBlock(pos, placeState, 2);
												++blocksPlaced;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return blocksPlaced > 0;
	}
}
