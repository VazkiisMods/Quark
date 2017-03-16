package vazkii.quark.world.world.underground;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.feature.UndergroundBiomes;

public class UndergroundBiomeLava extends BasicUndergroundBiome {
	
	int lavaChance, obsidianChance;
	boolean usePackedIce;

	public UndergroundBiomeLava() {
		super(Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), true);
	}
	
	@Override
	public void fillCeiling(World world, BlockPos pos, IBlockState state) {
		if(UndergroundBiomes.firestoneEnabled && world.rand.nextBoolean())
			world.setBlockState(pos, UndergroundBiomes.firestoneState, 2);
		else super.fillCeiling(world, pos, state);
	}
	
	@Override
	public void fillWall(World world, BlockPos pos, IBlockState state) {
		if(UndergroundBiomes.firestoneEnabled)
			world.setBlockState(pos, UndergroundBiomes.firestoneState, 2);
		else super.fillWall(world, pos, state);
	}
	
	@Override
	public void fillFloor(World world, BlockPos pos, IBlockState state) {
		if(lavaChance > 0 && !isBorder(world, pos, state) && world.rand.nextInt(lavaChance) == 0)
			world.setBlockState(pos, Blocks.LAVA.getDefaultState());
		else if(obsidianChance > 0 && world.rand.nextInt(obsidianChance) == 0)
			world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState(), 2);
		else if(UndergroundBiomes.firestoneEnabled)
			world.setBlockState(pos, UndergroundBiomes.firestoneState, 2);
		else super.fillFloor(world, pos, state);
	}
	
	@Override
	public void setupConfig(String category) {
		lavaChance = ModuleLoader.config.getInt("Lava Chance", category, 4, 0, Integer.MAX_VALUE, "The higher, the less lava will spawn");
		obsidianChance = ModuleLoader.config.getInt("Obsidian Chance", category, 16, 0, Integer.MAX_VALUE, "The higher, the less obsidian will spawn");
	}
}
