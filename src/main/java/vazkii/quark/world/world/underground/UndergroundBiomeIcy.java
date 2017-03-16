package vazkii.quark.world.world.underground;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.block.BlockBiomeCobblestone;
import vazkii.quark.world.feature.UndergroundBiomes;

public class UndergroundBiomeIcy extends BasicUndergroundBiome {
	
	int stalagmiteChance;
	boolean usePackedIce;

	public UndergroundBiomeIcy() {
		super(Blocks.ICE.getDefaultState(), null, null, true);
	}
	
	@Override
	public void fillCeiling(World world, BlockPos pos, IBlockState state) {
		if(UndergroundBiomes.icystoneEnabled)
			world.setBlockState(pos, UndergroundBiomes.icystoneState, 2);
	}
	
	@Override
	public void fillWall(World world, BlockPos pos, IBlockState state) {
		fillCeiling(world, pos, state);
	}
	
	@Override
	public void fillFloor(World world, BlockPos pos, IBlockState state) {
		IBlockState placeState = floorState;
		if(usePackedIce)
			placeState = Blocks.PACKED_ICE.getDefaultState();
		
		BlockPos placePos = pos;
		world.setBlockState(pos, placeState, 2);
		
		if(stalagmiteChance > 0 && world.rand.nextInt(stalagmiteChance) == 0) {
			int height = 3 + world.rand.nextInt(3);
			for(int i = 0; i < height; i++) {
				placePos = placePos.up();
				IBlockState stateAt = world.getBlockState(placePos);
				
				if(world.getBlockState(placePos).getBlock().isAir(stateAt, world, placePos))
					world.setBlockState(placePos, placeState, 2);
				else break;
			}
		}
	}
	
	@Override
	public void setupConfig(String category) {
		stalagmiteChance = ModuleLoader.config.getInt("Stalagmite Chance", category, 60, 0, Integer.MAX_VALUE, "The higher, the less stalagmites will spawn");
		usePackedIce = ModuleLoader.config.getBoolean("Use Packed Ice", category, true, "");
	}

}
