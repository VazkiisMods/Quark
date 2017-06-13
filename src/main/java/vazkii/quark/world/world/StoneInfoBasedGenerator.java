package vazkii.quark.world.world;

import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.handler.BiomeTypeConfigHandler;
import vazkii.quark.world.feature.RevampStoneGen;
import vazkii.quark.world.feature.RevampStoneGen.StoneInfo;

public class StoneInfoBasedGenerator {

	public Supplier<StoneInfo> infoSupplier;
	public String name;
	
	WorldGenMinable generator;
	long seedXor;
	
	// The lock has to be present to prevent chunk bleeding with really large values
	boolean lock;

	public StoneInfoBasedGenerator(Supplier<StoneInfo> infoSupplier, IBlockState state, String name) {
		this.infoSupplier = infoSupplier;
		this.name = name;

		generator = new WorldGenMinable(state, infoSupplier.get().clusterSize);
		seedXor = name.hashCode();
	}

	public void generate(int chunkX, int chunkZ, World world) {
		if(lock)
			return;
		
		StoneInfo info = infoSupplier.get();
		if(!info.enabled || !info.dims.canSpawnHere(world))
			return;
		
		Random rand = new Random(world.getSeed());
        long xSeed = rand.nextLong() >> 2 + 1L;
        long zSeed = rand.nextLong() >> 2 + 1L;
        long chunkSeed = (xSeed * chunkX + zSeed * chunkZ) ^ world.getSeed() ^ seedXor;
		rand.setSeed(chunkSeed);

		int amount = 1;
		int chance = info.clusterRarity;

		if(info.clustersRarityPerChunk) {
			chance = 1;
			amount = info.clusterRarity;
		}

		int lower = Math.abs(info.lowerBound);
		int range = Math.abs(info.upperBound - info.lowerBound);

		if(rand.nextInt(chance) == 0) {
			lock = true;
			for(int i = 0; i < amount; i++) {
				int x = chunkX * 16 + rand.nextInt(16) + 8;
				int y = rand.nextInt(range) + lower;
				int z = chunkZ * 16 + rand.nextInt(16) + 8;

				BlockPos pos = new BlockPos(x, y, z);

				if(RevampStoneGen.generateBasedOnBiomes) {
					Biome biome = world.getBiome(pos);
					
					if(!canGenerateInBiome(info, biome))
						continue;
				}

				generator.generate(world, rand, pos);
			}
			lock = false;
		}
	}
	
	public boolean canGenerateInBiome(Biome b) {
		return canGenerateInBiome(infoSupplier.get(), b);
	}
	
	public boolean canGenerateInBiome(StoneInfo info, Biome b) {
		return BiomeTypeConfigHandler.biomeTypeIntersectCheck(info.allowedBiomes, b);
	}
	


}
