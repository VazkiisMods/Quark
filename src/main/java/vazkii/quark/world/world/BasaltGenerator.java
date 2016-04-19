/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 15:17:15 (GMT)]
 */
package vazkii.quark.world.world;

import java.util.Random;

import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.world.feature.Basalt;

public class BasaltGenerator implements IWorldGenerator {

	boolean nether, overworld;
	int clusterCountOverworld, clusterCountNether;
	WorldGenMinable generatorOverworld;
	WorldGenMinable generatorNether;

	public BasaltGenerator(boolean nether, boolean overworld, int clusterSizeOverworld, int clusterSizeNether, int clusterCountOverworld, int clusterCountNether) {
		this.nether = nether;
		this.overworld = overworld;
		this.clusterCountNether = clusterCountNether;
		this.clusterCountOverworld = clusterCountOverworld;

		generatorOverworld = new WorldGenMinable(Basalt.basalt.getDefaultState(), clusterSizeOverworld);
		generatorNether = new WorldGenMinable(Basalt.basalt.getDefaultState(), clusterSizeNether, BlockMatcher.forBlock(Blocks.netherrack));
	}

	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		boolean isNether = world.provider instanceof WorldProviderHell;
		boolean isOverworld = world.provider instanceof WorldProviderSurface;

		if(isNether && !nether || isOverworld && !overworld || !isNether && !isOverworld)
			return;

		for(int i = 0; i < (isNether ? clusterCountNether : clusterCountOverworld); i++) {
			int x = chunkX * 16 + rand.nextInt(16);
			int y = rand.nextInt(isNether ? 128 : 80);
			int z = chunkZ * 16 + rand.nextInt(16);

			if(isNether)
				generatorNether.generate(world, rand, new BlockPos(x, y, z));
			else generatorOverworld.generate(world, rand, new BlockPos(x, y, z));
		}
	}

}
