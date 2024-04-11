package org.violetmoon.quark.content.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.violetmoon.quark.content.world.module.BlossomTreesModule;
import org.violetmoon.zeta.block.ZetaLeavesBlock;
import org.violetmoon.zeta.module.ZetaModule;

public class BlossomLeavesBlock extends ZetaLeavesBlock {

	public BlossomLeavesBlock(String id, @Nullable ZetaModule module, MapColor color) {
		super(id, module, color);
	}

	@Override
	public void animateTick(@NotNull BlockState stateIn, Level worldIn, BlockPos pos, @NotNull RandomSource rand) {
		if(BlossomTreesModule.dropLeafParticles && rand.nextInt(5) == 0 && worldIn.isEmptyBlock(pos.below())) {
			double windStrength = 5 + Math.cos((double) worldIn.getGameTime() / 2000) * 2;
			double windX = Math.cos((double) worldIn.getGameTime() / 1200) * windStrength;
			double windZ = Math.sin((double) worldIn.getGameTime() / 1000) * windStrength;

			worldIn.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, stateIn), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, windX, -1.0, windZ);
		}
		//for IW
		super.animateTick(stateIn, worldIn, pos, rand);
	}

}
