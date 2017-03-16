package vazkii.quark.world.world.underground;

import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.module.ModuleLoader;

public class UndergroundBiomePrismarine extends BasicUndergroundBiome {

	int seaLanternChance, waterChance;
	
	public UndergroundBiomePrismarine() {
		super(Blocks.PRISMARINE.getDefaultState(), Blocks.PRISMARINE.getDefaultState(), Blocks.PRISMARINE.getDefaultState());
	}
	
	@Override
	public void fillWall(World world, BlockPos pos, IBlockState state) {
		super.fillWall(world, pos, state);
		
		if(seaLanternChance > 0 && world.rand.nextInt(seaLanternChance) == 0)
			world.setBlockState(pos, Blocks.SEA_LANTERN.getDefaultState(), 2);
	}
	
	@Override
	public void fillFloor(World world, BlockPos pos, IBlockState state) {
		if(waterChance > 0 && !isBorder(world, pos, state) && world.rand.nextInt(waterChance) == 0)
			world.setBlockState(pos, Blocks.WATER.getDefaultState());
		else super.fillFloor(world, pos, state);
	}
	
	@Override
	public void setupConfig(String category) {
		seaLanternChance = ModuleLoader.config.getInt("Sea Lantern Chance", category, 120, 0, Integer.MAX_VALUE, "The higher, the less sea lanterns will spawn");
		waterChance = ModuleLoader.config.getInt("Water Chance", category, 4, 0, Integer.MAX_VALUE, "The higher, the less water will spawn");
	}
	
	
}
