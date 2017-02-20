package vazkii.quark.world.world;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.world.feature.RevampStoneGen.StoneInfo;

public class StoneInfoBasedGenerator implements IWorldGenerator {

	Supplier<StoneInfo> infoSupplier;
	WorldGenMinable generator;
	long seedXor;
	
	public StoneInfoBasedGenerator(Supplier<StoneInfo> infoSupplier, IBlockState state, String name) {
		this.infoSupplier = infoSupplier;
		
		generator = new WorldGenMinable(state, infoSupplier.get().clusterSize);
		seedXor = name.hashCode();
	}

	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        rand.setSeed(rand.nextLong() ^ seedXor);
		
		StoneInfo info = infoSupplier.get();
		if(!info.enabled)
			return;
		
		if(world.provider.getDimensionType() != DimensionType.OVERWORLD)
			return;
		
		int amount = 1;
		int chance = info.clusterRarity;
		
		if(info.clustersRarityPerChunk) {
			chance = 1;
			amount = info.clusterRarity;
		}
		
		int lower = Math.abs(info.lowerBound);
		int range = Math.abs(info.upperBound - info.lowerBound);
		
		if(rand.nextInt(chance) == 0)
			for(int i = 0; i < amount; i++) {
				int x = chunkX * 16 + rand.nextInt(16);
				int y = rand.nextInt(range) + lower;
				int z = chunkZ * 16 + rand.nextInt(16);
				
				generator.generate(world, rand, new BlockPos(x, y, z));
			}
	}
	
}
