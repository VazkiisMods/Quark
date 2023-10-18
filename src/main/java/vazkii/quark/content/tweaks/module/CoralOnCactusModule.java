package vazkii.quark.content.tweaks.module;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CoralFanBlock;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = "tweaks")
public class CoralOnCactusModule extends QuarkModule {

	private static boolean staticEnabled;
	
	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}
	
	public static boolean scanForWater(BlockState state, BlockGetter world, BlockPos pos, boolean prevValue) {
		if(prevValue || !staticEnabled)
			return prevValue;
		
		if(state.getBlock() instanceof CoralFanBlock)
			return world.getBlockState(pos.below()).getBlock() == Blocks.CACTUS;
		
		return false;
	}
	
}
