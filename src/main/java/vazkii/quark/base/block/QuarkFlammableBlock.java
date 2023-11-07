package vazkii.quark.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.zeta.module.ZetaModule;

public class QuarkFlammableBlock extends QuarkBlock {

	private final int flammability;

	public QuarkFlammableBlock(String regname, ZetaModule module, CreativeModeTab creativeTab, int flamability, Properties properties) {
		super(regname, module, creativeTab, properties);
		this.flammability = flamability;
	}

	@Override
	public boolean isFlammableZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return true;
	}

	@Override
	public int getFlammabilityZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return flammability;
	}

}
