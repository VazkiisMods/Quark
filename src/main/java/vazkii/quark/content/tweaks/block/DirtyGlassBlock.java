package vazkii.quark.content.tweaks.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.block.QuarkGlassBlock;
import vazkii.zeta.module.ZetaModule;

/**
 * @author WireSegal
 * Created at 12:49 PM on 8/24/19.
 */
public class DirtyGlassBlock extends QuarkGlassBlock {

	private static final float[] BEACON_COLOR_MULTIPLIER = new float[] { 0.25F, 0.125F, 0F };

	public DirtyGlassBlock(String regname, ZetaModule module, CreativeModeTab creativeTab, Properties properties) {
		super(regname, module, creativeTab, true, properties);
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplierZeta(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return BEACON_COLOR_MULTIPLIER;
	}

}
