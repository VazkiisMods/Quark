package org.violetmoon.quark.content.world.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;

import org.violetmoon.quark.content.world.module.GlimmeringWealdModule;

import java.util.Arrays;
import java.util.List;

public class GlowExtrasFeature extends Feature<NoneFeatureConfiguration> {

	public GlowExtrasFeature() {
		super(NoneFeatureConfiguration.CODEC);
	}

	public static List<PlacementModifier> placed() {
		return Arrays.asList(CountPlacement.of(200),
				InSquarePlacement.spread(),
				PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
				RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> config) {
		WorldGenLevel worldgenlevel = config.level();
		BlockPos blockpos = config.origin();
		RandomSource rng = config.random();

		MutableBlockPos setPos = new MutableBlockPos(blockpos.getX(), blockpos.getY(), blockpos.getZ());
		final int horiz = 2;
		final int vert = 6;
		final float chance = 0.5F;

		for(int i = -horiz; i < horiz + 1; i++)
			for(int j = -horiz; j < horiz + 1; j++)
				for(int k = -vert; k < vert + 1; k++) {
					setPos.set(blockpos.getX() + i, blockpos.getY() + k, blockpos.getZ() + j);

					if(rng.nextFloat() < chance && worldgenlevel.isStateAtPosition(setPos, BlockState::isAir)) {
						double res = rng.nextDouble();
						if(res > 0.85) { // try to place shrub
							if(worldgenlevel.isStateAtPosition(setPos.below(), s -> s.getBlock() == Blocks.DEEPSLATE))
								worldgenlevel.setBlock(setPos, GlimmeringWealdModule.glow_lichen_growth.defaultBlockState(), 2);
						}

						else if(res > 0.35) { // try to place lichen
							for(Direction dir : Direction.values()) {
								if(worldgenlevel.isStateAtPosition(setPos.relative(dir), s -> s.getBlock() == Blocks.DEEPSLATE)) {
									BlockState place = Blocks.GLOW_LICHEN.defaultBlockState();
									for(Direction dir2 : Direction.values()) {
										BooleanProperty prop = GlowLichenBlock.getFaceProperty(dir2);
										place = place.setValue(prop, dir == dir2);
									}

									worldgenlevel.setBlock(setPos, place, 2);
									break;
								}
							}
						}
					}
				}

		return true;
	}

}
