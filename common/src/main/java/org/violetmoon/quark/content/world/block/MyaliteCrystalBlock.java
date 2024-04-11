package org.violetmoon.quark.content.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import org.violetmoon.zeta.block.OldMaterials;
import org.violetmoon.zeta.block.ZetaGlassBlock;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.IZetaBlockColorProvider;

public class MyaliteCrystalBlock extends ZetaGlassBlock implements IZetaBlockColorProvider {

	public MyaliteCrystalBlock(@Nullable ZetaModule module) {
		super("myalite_crystal", module, true,
				OldMaterials.glass()
						.mapColor(DyeColor.PURPLE)
						.strength(0.5F, 1200F)
						.sound(SoundType.GLASS)
						.lightLevel(b -> 14)
						.requiresCorrectToolForDrops()
						.randomTicks()
						.noOcclusion());

		if(module == null) //auto registration below this line
			return;
		setCreativeTab(CreativeModeTabs.NATURAL_BLOCKS);
	}

	private static float[] decompColor(int color) {
		int r = (color & 0xFF0000) >> 16;
		int g = (color & 0xFF00) >> 8;
		int b = color & 0xFF;
		return new float[] { (float) r / 255.0F, (float) g / 255.0F, (float) b / 255.0F };
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplierZeta(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return decompColor(MyaliteColorLogic.getColor(pos));
	}

	@Override
	public @Nullable String getBlockColorProviderName() {
		return "myalite";
	}

	@Override
	public @Nullable String getItemColorProviderName() {
		return "myalite";
	}
}
