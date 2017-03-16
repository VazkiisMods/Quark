package vazkii.quark.world.world.underground;

import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.module.ModuleLoader;

public class UndergroundBiomeOvergrown extends BasicUndergroundBiome {

	int rootChance, dirtChance;
	
	public UndergroundBiomeOvergrown() {
		super(Blocks.MOSSY_COBBLESTONE.getDefaultState(), Blocks.LEAVES.getDefaultState().withProperty(BlockLeaves.DECAYABLE, false), null);
	}

	@Override
	public void finalCeilingPass(World world, BlockPos pos) {
		if(rootChance > 0 && world.rand.nextInt(rootChance) == 0) {
			int count = 0;
			for(int i = 0; i < 20; i++) {
				BlockPos checkPos = pos.add(0, -i, 0);
				if(isFloor(world, checkPos, world.getBlockState(checkPos))) {
					count = i;
					break;
				}
			}
			
			for(int i = 0; i <= count; i++) {
				BlockPos placePos = pos.add(0, -i, 0);
				world.setBlockState(placePos, Blocks.LOG.getDefaultState());
			}
			
		}
	}
	
	@Override
	public void fillFloor(World world, BlockPos pos, IBlockState state) {
		if(dirtChance > 0 && world.rand.nextInt(dirtChance) == 0)
			world.setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT));
		else super.fillFloor(world, pos, state);
	}
	
	@Override
	public void setupConfig(String category) {
		rootChance = ModuleLoader.config.getInt("Root Chance", category, 40, 0, Integer.MAX_VALUE, "The higher, the less roots will spawn");
		dirtChance = ModuleLoader.config.getInt("Dirt Chance", category, 2, 0, Integer.MAX_VALUE, "The higher, the less dirt will spawn");
	}

}
