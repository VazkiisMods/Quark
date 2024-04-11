package org.violetmoon.quark.content.tweaks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import org.violetmoon.zeta.block.ZetaGlassBlock;
import org.violetmoon.zeta.module.ZetaModule;

/**
 * @author WireSegal
 *         Created at 12:49 PM on 8/24/19.
 */
public class DirtyGlassBlock extends ZetaGlassBlock {

	private static final float[] BEACON_COLOR_MULTIPLIER = new float[] { 0.25F, 0.125F, 0F };

	public DirtyGlassBlock(String regname, @Nullable ZetaModule module, Properties properties) {
		super(regname, module, true, properties);

		if(module == null) //auto registration below this line
			return;
		setCreativeTab(CreativeModeTabs.COLORED_BLOCKS, Blocks.TINTED_GLASS, false);
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplierZeta(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return BEACON_COLOR_MULTIPLIER;
	}

}
