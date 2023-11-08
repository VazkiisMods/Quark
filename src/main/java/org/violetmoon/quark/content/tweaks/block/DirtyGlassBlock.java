package org.violetmoon.quark.content.tweaks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.violetmoon.quark.base.block.QuarkGlassBlock;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nullable;

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
