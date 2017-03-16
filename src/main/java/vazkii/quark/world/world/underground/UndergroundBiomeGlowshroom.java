package vazkii.quark.world.world.underground;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.feature.UndergroundBiomes;

public class UndergroundBiomeGlowshroom extends BasicUndergroundBiome {

	int mushroomChance;
	
	public UndergroundBiomeGlowshroom() {
		super(Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState());
	}
	
	@Override
	public void fillFloor(World world, BlockPos pos, IBlockState state) {
		if(UndergroundBiomes.glowceliumEnabled) {
			world.setBlockState(pos, UndergroundBiomes.glowcelium.getDefaultState());
			
			if(mushroomChance > 0 && world.rand.nextInt(mushroomChance) == 0) 
				world.setBlockState(pos.up(), UndergroundBiomes.glowshroom.getDefaultState());
		} else { 
			super.fillFloor(world, pos, state);
			
			if(mushroomChance > 0 && world.rand.nextInt(mushroomChance) == 0) 
				world.setBlockState(pos.up(), (world.rand.nextBoolean() ? Blocks.BROWN_MUSHROOM : Blocks.RED_MUSHROOM).getDefaultState());
		}
	}
	
	@Override
	public void setupConfig(String category) {
		mushroomChance = ModuleLoader.config.getInt("Mushroom Chance", category, 15, 0, Integer.MAX_VALUE, "The higher, the less mushrooms will spawn");
	}

}
