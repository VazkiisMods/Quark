package vazkii.quark.world.world;

import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.world.feature.FairyRings;

public class FairyRingGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if(!FairyRings.dimensions.canSpawnHere(world))
			return;
		
		int x = chunkX * 16 + 8 + random.nextInt(16);
		int z = chunkZ * 16 + 8 + random.nextInt(16);
		Biome biome = world.getBiome(new BlockPos(x, 0, z));
		Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
		int chance = 0;
		if(types.contains(BiomeDictionary.Type.FOREST))
			chance = FairyRings.forestChance;
		else if(types.contains(BiomeDictionary.Type.PLAINS))
			chance = FairyRings.plainsChance;
		
		if(chance > 0 && random.nextInt(chance) == 0) {
			BlockPos pos = new BlockPos(x, 128, z);
			IBlockState state = world.getBlockState(pos);
			
			while(state.getMaterial() != Material.GRASS && pos.getY() > 30) {
				pos = pos.down();
				state = world.getBlockState(pos);
			}
			
			if(state.getMaterial() == Material.GRASS)
				spawnFairyRing(world, pos.down());
		}
	}
	
	public static void spawnFairyRing(World world, BlockPos pos) {
		IBlockState flower = Blocks.RED_FLOWER.getStateFromMeta(8);
		for(int i = -3; i <= 3; i++)
			for(int j = -3; j <= 3; j++) {
				float dist = (i * i) + (j * j);
				if(dist < 7 || dist > 10)
					continue;
				
				for(int k = 5; k > -4; k--) {
					BlockPos fpos = pos.add(i, k, j);
					BlockPos fposUp = fpos.up();
					IBlockState state = world.getBlockState(fpos);
					IBlockState stateUp = world.getBlockState(fposUp);
					if(state.getMaterial() == Material.GRASS && (world.isAirBlock(fposUp) || stateUp.getBlock().isReplaceable(world, fposUp))) {
						world.setBlockState(fpos.up(), flower);
						break;
					}
				}
			}
		
		BlockPos orePos = pos.down(world.rand.nextInt(10) + 25);
		IBlockState stoneState = world.getBlockState(orePos);
		int down = 0;
		while((stoneState.getBlock() != Blocks.STONE || !(stoneState.getValue(BlockStone.VARIANT).isNatural())) && down < 10) {
			orePos = orePos.down();
			stoneState = world.getBlockState(orePos);
			down++;
		}
		
		if(stoneState.getBlock() == Blocks.STONE && stoneState.getValue(BlockStone.VARIANT).isNatural()) {
			Block block = FairyRings.ores.get(world.rand.nextInt(FairyRings.ores.size()));
			IBlockState ore = (world.rand.nextBoolean() ? Blocks.DIAMOND_ORE : Blocks.EMERALD_ORE).getDefaultState();
			world.setBlockState(orePos, ore);
			for(EnumFacing face : EnumFacing.VALUES)
				if(world.rand.nextBoolean())
					world.setBlockState(orePos.offset(face), ore);
		}
	}

}
