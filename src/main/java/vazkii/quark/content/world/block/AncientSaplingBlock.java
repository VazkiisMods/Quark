package vazkii.quark.content.world.block;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.LeavesBlock;
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
import org.jetbrains.annotations.NotNull;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.block.QuarkSaplingBlock;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.world.module.AncientWoodModule;

public class AncientSaplingBlock extends QuarkSaplingBlock {

	public AncientSaplingBlock(QuarkModule module) {
		super("ancient", module, new AncientTree());
	}

	public static class AncientTree extends AbstractTreeGrower {

		public final TreeConfiguration config;

		public AncientTree() {
			config = (new TreeConfiguration.TreeConfigurationBuilder(
					BlockStateProvider.simple(AncientWoodModule.woodSet.log),
					new MultiFoliageStraightTrunkPlacer(17, 4, 6, ConstantInt.of(5), ConstantInt.of(3), ConstantInt.of(3)),
					BlockStateProvider.simple(AncientWoodModule.ancient_leaves),
					new FancyFoliagePlacer(UniformInt.of(2, 4), ConstantInt.of(0) , 2),
					new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))))
					.decorators(Lists.newArrayList(new AncientTreeTopperDecorator(
						BlockStateProvider.simple(AncientWoodModule.ancient_leaves.defaultBlockState().setValue(LeavesBlock.DISTANCE, 1))))
					)
					.ignoreVines()
					.build();
		}

		@Override
		protected Holder<ConfiguredFeature<TreeConfiguration, ?>> getConfiguredFeature(@Nonnull RandomSource rand, boolean beehive) {
			return Holder.direct(new ConfiguredFeature<>(Feature.TREE, config));
		}

	}
	
	public static class MultiFoliageStraightTrunkPlacer extends TrunkPlacer {

		private final IntProvider foliageDistance;
		private final IntProvider maxBlobs;
		private final IntProvider freeTopHeight;
		
		public MultiFoliageStraightTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider foliageDistance, IntProvider maxBlobs, IntProvider freeTopHeight) {
			super(baseHeight, heightRandA, heightRandB);
			this.foliageDistance = foliageDistance;
			this.maxBlobs = maxBlobs;
			this.freeTopHeight = freeTopHeight;
		}

		@Override
		public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, int freeTreeHeight, BlockPos pos, TreeConfiguration config) {
			setDirtAt(level, blockSetter, random, pos.below(), config);

			List<BlockPos> foliagePositions = new ArrayList<>();

			int sampledMaxBlobs = maxBlobs.sample(random);
			int placed = 0;
			int j = freeTopHeight.sample(random);
			for(int i = freeTreeHeight; i >= 0; --i) {
				BlockPos target = pos.above(i);
				this.placeLog(level, blockSetter, random, target, config);
				
				if(placed < sampledMaxBlobs) {
					if(j == 0) {
						foliagePositions.add(target);
						j = foliageDistance.sample(random);
						placed++;
					} else j--;
				}
				
			}

			return foliagePositions.stream().map(p -> new FoliagePlacer.FoliageAttachment(p, 0, false)).collect(Collectors.toList());
		}

		@Override
		protected TrunkPlacerType<?> type() {
			return Type.ANCIENT_TREE_TOPPER;
		}

		public static class Type extends TrunkPlacerType<MultiFoliageStraightTrunkPlacer> {
			public static final TrunkPlacerType<MultiFoliageStraightTrunkPlacer> ANCIENT_TREE_TOPPER = register();

			private static final Codec<MultiFoliageStraightTrunkPlacer> CODEC = RecordCodecBuilder.create(
				instance -> trunkPlacerParts(instance)
					.and(IntProvider.NON_NEGATIVE_CODEC.fieldOf("foliage_distance").forGetter(trunkPlacer -> trunkPlacer.foliageDistance))
					.and(IntProvider.NON_NEGATIVE_CODEC.fieldOf("max_blobs").forGetter(trunkPlacer -> trunkPlacer.maxBlobs))
					.and(IntProvider.NON_NEGATIVE_CODEC.fieldOf("free_top_height").forGetter(trunkPlacer -> trunkPlacer.freeTopHeight))
					.apply(instance, MultiFoliageStraightTrunkPlacer::new)
			);

			private static TrunkPlacerType<MultiFoliageStraightTrunkPlacer> register() {
				Type t = new Type();
				RegistryHelper.register(t, "multi_foliage_straight_trunk_placer", Registry.TRUNK_PLACER_TYPE_REGISTRY);
				return t;
			}

			public static void init() {}

			private Type() {
				super(CODEC);
			}
		}
	}
	
	public static class AncientTreeTopperDecorator extends TreeDecorator {

		private final BlockStateProvider foliageProvider;

		public AncientTreeTopperDecorator(BlockStateProvider foliageProvider) {
			this.foliageProvider = foliageProvider;
		}

		@Override
		public void place(Context ctx) {
			Optional<BlockPos> pos = ctx.logs().stream().max(Comparator.comparingInt(Vec3i::getY));
			if(pos.isPresent()) {
				BlockPos top = pos.get();
				
				ImmutableSet<BlockPos> leafPos = ImmutableSet.of(
						top.above(), top.east(), top.west(), top.north(), top.south()
				);
				
				leafPos.forEach(p -> {
					if(ctx.isAir(p))
						ctx.setBlock(p, foliageProvider.getState(ctx.random(), p));
				});
			}
		}
		
		@Override
		protected @NotNull TreeDecoratorType<?> type() {
			return Type.ANCIENT_TREE_TOPPER;
		}

		public static class Type extends TreeDecoratorType<AncientTreeTopperDecorator> {
			public static final TreeDecoratorType<AncientTreeTopperDecorator> ANCIENT_TREE_TOPPER = register();

			private static final Codec<AncientTreeTopperDecorator> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
					BlockStateProvider.CODEC.fieldOf("foliage_provider").forGetter(decorator -> decorator.foliageProvider)
				).apply(instance, AncientTreeTopperDecorator::new)
			);

			private static TreeDecoratorType<AncientTreeTopperDecorator> register() {
				Type t = new Type();
				RegistryHelper.register(t, "ancient_tree_topper", Registry.TREE_DECORATOR_TYPE_REGISTRY);
				return t;
			}

			public static void init() {}

			private Type() {
				super(CODEC);
			}
		}
		
	}

}
