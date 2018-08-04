package vazkii.quark.world.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.world.block.BlockSpeleothem;
import vazkii.quark.world.block.BlockSpeleothem.EnumSize;
import vazkii.quark.world.feature.Basalt;
import vazkii.quark.world.feature.RevampStoneGen;
import vazkii.quark.world.feature.Speleothems;

public class SpeleothemGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if(!Speleothems.dimensionConfig.canSpawnHere(world))
			return;
		
		int x = chunkX * 16 + 8;
		int z = chunkZ * 16 + 8;
		
		int spread = 16;
		int tries = Speleothems.tries;
		int innerSpread = 6;
		int innerTries = Speleothems.clusterCount;
		int upperBound = world.getSeaLevel();
		int offset = 6;
		
		if(world.provider.isNether()) {
			upperBound = 128;
			offset = 0;
			tries = Speleothems.netherTries;
			innerSpread = Speleothems.netherClusterCount;
		}
		
		for(int i = 0; i < tries; i++) {
			BlockPos target = new BlockPos(x + random.nextInt(spread), random.nextInt(upperBound) + offset, z + random.nextInt(spread));
			if(placeSpeleothemCluster(random, world, target, innerSpread, innerTries))
				i++;
		}
	}
	
	private boolean placeSpeleothemCluster(Random random, World world, BlockPos pos, int spread, int tries) {
		if(!findAndPlaceSpeleothem(random, world, pos))
			return false;
		
		for(int i = 0; i < tries; i++) {
			BlockPos target = pos.add(random.nextInt(spread * 2 + 1) - spread, random.nextInt(spread + 1) - spread / 1, random.nextInt(spread * 2 + 1) - spread);
			findAndPlaceSpeleothem(random, world, target);
		}
		
		return true;
	}
	
	private boolean findAndPlaceSpeleothem(Random random, World world, BlockPos pos) {
		if(!world.isAirBlock(pos))
			return false;
		
		boolean up = random.nextBoolean();
		EnumFacing diff = up ? EnumFacing.UP : EnumFacing.DOWN;
		IBlockState stateAt = null;
		do {
			pos = pos.offset(diff);
			stateAt = world.getBlockState(pos);
		} while(pos.getY() > 4 && pos.getY() < 200 && !stateAt.getBlock().isFullBlock(stateAt));
		
		Block type = getSpeleothemType(stateAt);
		placeSpeleothem(random, world, pos, type, !up);
		
		return true;
	}
		
	private void placeSpeleothem(Random random, World world, BlockPos pos, Block type, boolean up) {
		if(type == null)
			return;
		
		EnumFacing diff = up ? EnumFacing.UP : EnumFacing.DOWN;
		int size = random.nextInt(3) == 0 ? 2 : 3;
		if(!up && random.nextInt(20) == 0)
			size = 1;
		
		for(int i = 0; i < size; i++) {
			pos = pos.offset(diff);
			if(!world.isAirBlock(pos))
				return;
			
			EnumSize sizeType = EnumSize.values()[size - i - 1];
			IBlockState targetBlock = type.getDefaultState().withProperty(BlockSpeleothem.SIZE, sizeType);
			world.setBlockState(pos, targetBlock);
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	private Block getSpeleothemType(IBlockState state) {
		Block block = state.getBlock();

		if(block == Blocks.NETHERRACK)
			return Speleothems.netherrack_speleothem;
		else if(block == Blocks.STONE) {
			switch(state.getValue(BlockStone.VARIANT)) {
			case STONE: 
				return Speleothems.stone_speleothem;
			case ANDESITE: 
				return Speleothems.andesite_speleothem;
			case GRANITE:
				return Speleothems.granite_speleothem;
			case DIORITE:
				return Speleothems.diorite_speleothem;
			}
		} else if(block == Basalt.basalt)
			return Speleothems.basalt_speleothem;
		else if(block == RevampStoneGen.marble)
			return Speleothems.marble_speleothem;
		else if(block == RevampStoneGen.limestone)
			return Speleothems.limestone_speleothem;
		
		return null;
	}

}
