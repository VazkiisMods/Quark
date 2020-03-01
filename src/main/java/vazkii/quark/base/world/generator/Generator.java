package vazkii.quark.base.world.generator;

import java.util.Random;
import java.util.function.BooleanSupplier;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import vazkii.quark.base.world.config.DimensionConfig;

public abstract class Generator implements IGenerator {
	
	public static final BooleanSupplier NO_COND = () -> true;
	
	public final DimensionConfig dimConfig;
	private final BooleanSupplier condition;
	
	public Generator(DimensionConfig dimConfig) {
		this(dimConfig, NO_COND);
	}
	
	public Generator(DimensionConfig dimConfig, BooleanSupplier condition) {
		this.dimConfig = dimConfig;
		this.condition = condition;
	}

	@Override
	public final int generate(int seedIncrement, long seed, GenerationStage.Decoration stage, IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, SharedSeedRandom rand, BlockPos pos) {
		rand.setFeatureSeed(seed, seedIncrement, stage.ordinal());
		generateChunk(worldIn, generator, rand, pos);
		return seedIncrement + 1;
	}

	public abstract void generateChunk(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos);

	@Override
	public boolean canGenerate(IWorld world) {
		return condition.getAsBoolean() && dimConfig.canSpawnHere(world.getWorld());
	}
	
	public Biome getBiome(IWorld world, BlockPos pos) {
		return world.getBiomeManager().getBiome(pos);
	}
	
}
