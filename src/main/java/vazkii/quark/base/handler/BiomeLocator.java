/*******************************************************************************
 * Copyright 2014-2019, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 * 
 * Original: https://github.com/Glitchfiend/BiomesOPlenty/blob/BOP-1.14.x-9.x.x/src/main/java/biomesoplenty/common/util/biome/BiomeUtil.java
 ******************************************************************************/
package vazkii.quark.base.handler;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraft.world.server.ServerChunkProvider;

public class BiomeLocator {

	public static BlockPos spiralOutwardsLookingForBiome(World world, Biome biomeToFind, double startX, double startZ) {
		AbstractChunkProvider provider = world.getChunkProvider();
		if(provider != null && provider instanceof ServerChunkProvider) {
			ServerChunkProvider sprovider = (ServerChunkProvider) provider;
			BiomeManager manager = world.getBiomeManager();
			if(manager != null) {
				int sampleSpacing = 4 << getBiomeSize(world, sprovider.generator);
				int maxDist = sampleSpacing * 100;
				return spiralOutwardsLookingForBiome(world, manager, biomeToFind, startX, startZ, maxDist, sampleSpacing);
			}
		}
		
		return null;
	}

	// sample points in an archimedean spiral starting from startX,startY each one sampleSpace apart
	// stop when the specified biome is found (and return the position it was found at) or when we reach maxDistance (and return null)
	public static BlockPos spiralOutwardsLookingForBiome(World world, BiomeManager manager, Biome biomeToFind, double startX, double startZ, int maxDist, int sampleSpace) {
		if(maxDist <= 0 || sampleSpace <= 0) 
			throw new IllegalArgumentException("maxDist and sampleSpace must be positive");

		double a = sampleSpace / Math.sqrt(Math.PI);
		double b = 2 * Math.sqrt(Math.PI);
		double x = 0;
		double z = 0;
		double dist = 0;

		for(int n = 0; dist < maxDist; ++n) {
			double rootN = Math.sqrt(n);
			dist = a * rootN;
			x = startX + (dist * Math.sin(b * rootN));
			z = startZ + (dist * Math.cos(b * rootN));

			// chunkManager.genBiomes is the first layer returned from initializeAllBiomeGenerators()
			// chunkManager.biomeIndexLayer is the second layer returned from initializeAllBiomeGenerators(), it's zoomed twice from genBiomes (>> 2) this one is actual size
			// chunkManager.getBiomeGenAt uses biomeIndexLayer to get the biome
			BlockPos pos = new BlockPos(x, world.getSeaLevel(), z);
			if(manager.getBiome(pos) == biomeToFind)
				return pos;
		}

		return null;
	}

	private static int getBiomeSize(World world, ChunkGenerator<?> generator) {
		int size = 4;

		GenerationSettings settings = generator.getSettings();
		if(settings instanceof OverworldGenSettings)
			size = ((OverworldGenSettings) settings).getBiomeSize();

		WorldType type = world.getWorldType();
		if(type == WorldType.LARGE_BIOMES)
			return 6;
		
		return LayerUtil.getModdedBiomeSize(type, size);
	}

}
