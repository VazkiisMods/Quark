package vazkii.quark.world.world;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import vazkii.quark.base.handler.BiomeTypeConfigHandler;
import vazkii.quark.world.feature.RevampStoneGen.StoneInfo;

public class StoneInfoBasedGenerator extends MultiChunkFeatureGenerator {

	public Supplier<StoneInfo> infoSupplier;
	public String name;
	
	IBlockState state;
	long seedXor;
	
	public StoneInfoBasedGenerator(Supplier<StoneInfo> infoSupplier, IBlockState state, String name) {
		this.infoSupplier = infoSupplier;
		this.name = name;

		this.state = state;
		seedXor = name.hashCode();
	}

	@Override
	public boolean canGenerate(World world, int chunkX, int chunkZ) {
		StoneInfo info = infoSupplier.get();
		return info.enabled && info.dims.canSpawnHere(world);
	}
	
	public boolean canGenerateInBiome(Biome b) {
		return BiomeTypeConfigHandler.biomeTypeIntersectCheck(infoSupplier.get().allowedBiomes, b);
	}
	
	@Override
	public boolean isSourceValid(World world, BlockPos pos) {
		return canGenerateInBiome(world.getBiome(pos));
	}
	
	@Override
	public long modifyWorldSeed(long seed) {
		return seed ^ seedXor;
	}

	@Override
	public int getFeatureRadius() {
		StoneInfo info = infoSupplier.get();
		return info.clusterSize;
	}

	@Override
	public void generateChunkPart(BlockPos src, Random random, int chunkX, int chunkZ, World world) {
		StoneInfo info = infoSupplier.get();
		forEachChunkBlock(chunkX, chunkZ, info.lowerBound - info.clusterSize, info.upperBound + info.clusterSize, (pos) -> {
			if(canPlaceBlock(world, pos) && pos.distanceSq(src) < (info.clusterSize * info.clusterSize))
				world.setBlockState(pos, state, 0);
		});
	}
	
	public boolean canPlaceBlock(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() == Blocks.STONE;
	}

	@Override
	public BlockPos[] getSourcesInChunk(Random random, int chunkX, int chunkZ, World world) {
		StoneInfo info = infoSupplier.get();
		int chance = info.clusterRarity;
		int amount = 1;

		if(info.clustersRarityPerChunk) {
			chance = 1;
			amount = info.clusterRarity;
		}
		
		BlockPos[] sources;
		if(chance > 0 && random.nextInt(chance) == 0) {
			sources = new BlockPos[amount];
			int lower = Math.abs(info.lowerBound);
			int range = Math.abs(info.upperBound - info.lowerBound);
			
			for(int i = 0; i < amount && range > 0; i++) {
				int x = chunkX * 16 + random.nextInt(16) + 8;
				int y = random.nextInt(range) + lower;
				int z = chunkZ * 16 + random.nextInt(16) + 8;

				BlockPos pos = new BlockPos(x, y, z);
				sources[i] = pos;
			}
		} else sources = new BlockPos[0];
			
		return sources;
	}
	
}
