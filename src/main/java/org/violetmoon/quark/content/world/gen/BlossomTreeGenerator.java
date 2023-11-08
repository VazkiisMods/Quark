package org.violetmoon.quark.content.world.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.material.Fluids;
import org.violetmoon.quark.base.world.generator.Generator;
import org.violetmoon.quark.content.world.block.BlossomSaplingBlock.BlossomTree;
import org.violetmoon.quark.content.world.config.BlossomTreeConfig;

import java.util.Optional;

public class BlossomTreeGenerator extends Generator {

	private BlossomTreeConfig config;
	private BlossomTree tree;

	public BlossomTreeGenerator(BlossomTreeConfig config, BlossomTree tree) {
		super(config.dimensions);
		this.config = config;
		this.tree = tree;
	}

	@Override
	public void generateChunk(WorldGenRegion worldIn, ChunkGenerator generator, RandomSource rand, BlockPos pos) {
		BlockPos placePos = pos.offset(rand.nextInt(16), 0, rand.nextInt(16));
		if(config.biomeConfig.canSpawn(getBiome(worldIn, placePos, false)) && rand.nextInt(config.rarity) == 0) {
			placePos = worldIn.getHeightmapPos(Types.MOTION_BLOCKING, placePos).below();

			BlockState state = worldIn.getBlockState(placePos);
			if(state.getBlock().canSustainPlant(state, worldIn, pos, Direction.UP, (SaplingBlock) Blocks.OAK_SAPLING)) {
				BlockPos up = placePos.above();
				BlockState upState = worldIn.getBlockState(up);
				if(upState.canBeReplaced(Fluids.WATER))
					worldIn.setBlock(up, Blocks.AIR.defaultBlockState(), 0);

				FeaturePlaceContext<TreeConfiguration> context = new FeaturePlaceContext<>(Optional.of(new ConfiguredFeature<>(Feature.TREE, tree.config)), worldIn, generator, rand, up, tree.config);
				Feature.TREE.place(context);
			}
		}
	}

}
