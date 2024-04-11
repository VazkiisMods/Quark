package org.violetmoon.quark.addons.oddities.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.violetmoon.quark.addons.oddities.block.MagnetBlock;
import org.violetmoon.quark.addons.oddities.magnetsystem.MagnetSystem;
import org.violetmoon.quark.addons.oddities.module.MagnetsModule;

public class MagnetBlockEntity extends BlockEntity {

	public MagnetBlockEntity(BlockPos pos, BlockState state) {
		super(MagnetsModule.magnetType, pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, MagnetBlockEntity be) {
		boolean powered = state.getValue(MagnetBlock.POWERED);

		if(powered) {
			Direction dir = state.getValue(MagnetBlock.FACING);
			int power = level.getBestNeighborSignal(pos);
			be.magnetize(state, dir, dir, power);
			be.magnetize(state, dir.getOpposite(), dir, power);
		}
	}

	private void magnetize(BlockState state, Direction dir, Direction moveDir, int power) {
		if(level == null)
			return;

		double magnitude = (dir == moveDir ? 1 : -1);

		double particleMotion = 0.05 * magnitude;
		double particleChance = 0.2;
		double xOff = dir.getStepX() * particleMotion;
		double yOff = dir.getStepY() * particleMotion;
		double zOff = dir.getStepZ() * particleMotion;

		for(int i = 1; i <= power; i++) {
			BlockPos targetPos = worldPosition.relative(dir, i);
			BlockState targetState = level.getBlockState(targetPos);

			if(targetState.getBlock() == MagnetsModule.magnetized_block)
				break;

			if(!level.isClientSide && targetState.getBlock() != Blocks.MOVING_PISTON && targetState.getBlock() != MagnetsModule.magnetized_block) {
				PushReaction reaction = MagnetSystem.getPushAction(this, targetPos, targetState, moveDir);
				if (reaction == PushReaction.IGNORE || reaction == PushReaction.DESTROY) {
					BlockPos frontPos = targetPos.relative(moveDir);
					BlockState frontState = level.getBlockState(frontPos);
					if (frontState.isAir())
						MagnetSystem.applyForce(level, targetPos, power - i + 1, dir == moveDir, moveDir, i, worldPosition);
				}
			}

			if (!targetState.isAir())
				break;

			if (!state.getValue(MagnetBlock.WAXED) && level.isClientSide && Math.random() <= particleChance) {
				double x = targetPos.getX() + (xOff == 0 ? 0.5 : Math.random());
				double y = targetPos.getY() + (yOff == 0 ? 0.5 : Math.random());
				double z = targetPos.getZ() + (zOff == 0 ? 0.5 : Math.random());
				level.addParticle(ParticleTypes.SNEEZE, x, y, z, xOff, yOff, zOff);
			}
		}
	}
}
