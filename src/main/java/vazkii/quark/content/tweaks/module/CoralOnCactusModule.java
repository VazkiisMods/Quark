package vazkii.quark.content.tweaks.module;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CoralFanBlock;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "tweaks")
public class CoralOnCactusModule extends QuarkModule {

	private static boolean staticEnabled;
	
	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
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
