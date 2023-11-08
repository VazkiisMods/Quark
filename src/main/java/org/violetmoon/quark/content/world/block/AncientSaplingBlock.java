package org.violetmoon.quark.content.world.block;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import org.violetmoon.quark.base.block.QuarkSaplingBlock;
import org.violetmoon.quark.content.world.module.AncientWoodModule;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class AncientSaplingBlock extends QuarkSaplingBlock {

	public AncientSaplingBlock(ZetaModule module) {
		super("ancient", module, new AncientTree());
	}

	public static class AncientTree extends AbstractTreeGrower {

		public final TreeConfiguration config;

		public AncientTree() {
			config = (new TreeConfiguration.TreeConfigurationBuilder(
					BlockStateProvider.simple(AncientWoodModule.woodSet.log),
					new MultiFolliageStraightTrunkPlacer(17, 4, 6, 5, 3),
					BlockStateProvider.simple(AncientWoodModule.ancient_leaves),
					new FancyFoliagePlacer(UniformInt.of(2, 4), ConstantInt.of(-3) , 2),
					new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))))
					.decorators(Lists.newArrayList(new AncientTreeTopperDecorator()))
					.ignoreVines()
					.build();
		}

		@Override
		protected Holder<ConfiguredFeature<TreeConfiguration, ?>> getConfiguredFeature(@Nonnull RandomSource rand, boolean hjskfsd) {
			return Holder.direct(new ConfiguredFeature<>(Feature.TREE, config));
		}

	}
	
	public static class MultiFolliageStraightTrunkPlacer extends TrunkPlacer {

		final int folliageDistance;
		final int maxBlobs;
		
		public MultiFolliageStraightTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, int folliageDistance, int maxBlobs) {
			super(baseHeight, heightRandA, heightRandB);
			this.folliageDistance = folliageDistance;
			this.maxBlobs = maxBlobs;
		}

		@Override
		public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader p_226147_, BiConsumer<BlockPos, BlockState> p_226148_, RandomSource p_226149_, int p_226150_, BlockPos p_226151_, TreeConfiguration p_226152_) {
			setDirtAt(p_226147_, p_226148_, p_226149_, p_226151_.below(), p_226152_);

			List<BlockPos> folliagePositions = new ArrayList<>();
			
			int placed = 0;
			int j = 0;
			for(int i = p_226150_; i >= 0; --i) {
				BlockPos target = p_226151_.above(i);
				this.placeLog(p_226147_, p_226148_, p_226149_, target, p_226152_);
				
				if(placed < maxBlobs) {
					if(j == 0) {
						folliagePositions.add(target);
						j = folliageDistance;
						placed++;
					} else j--;
				}
				
			}

			return folliagePositions.stream().map(p -> new FoliagePlacer.FoliageAttachment(p, 0, false)).collect(Collectors.toList());
		}

		@Override
		protected TrunkPlacerType<?> type() {
			return null;
		}
	}
	
	public static class AncientTreeTopperDecorator extends TreeDecorator {

		@Override
		public void place(Context ctx) {
			Optional<BlockPos> pos = ctx.logs().stream().max((a, b) -> a.getY() - b.getY());
			if(pos.isPresent()) {
				BlockPos top = pos.get();
				
				ImmutableSet<BlockPos> leafPos = ImmutableSet.of(
						top.above(), top.east(), top.west(), top.north(), top.south()
				);
				
				BlockState state = AncientWoodModule.ancient_leaves.defaultBlockState();
				leafPos.forEach(p -> {
					if(ctx.isAir(p))
						ctx.setBlock(p, state);
				});
			}
		}
		
		@Override
		protected TreeDecoratorType<?> type() {
			return null;
		}

		
	}

}
