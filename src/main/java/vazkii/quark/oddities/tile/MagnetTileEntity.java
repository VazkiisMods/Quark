package vazkii.quark.oddities.tile;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import vazkii.quark.oddities.block.MagnetBlock;
import vazkii.quark.oddities.magnetsystem.MagnetSystem;
import vazkii.quark.oddities.module.MagnetsModule;

public class MagnetTileEntity extends TileEntity implements ITickableTileEntity {

	public MagnetTileEntity() {
		super(MagnetsModule.magnetType);
	}

	@Override
	public void tick() {
		BlockState state = getBlockState();
		boolean powered = state.get(MagnetBlock.POWERED);

		if(powered) {
			Direction dir = state.get(MagnetBlock.FACING);
			int power = getPower(dir);
			magnetize(dir, dir, power);
			magnetize(dir.getOpposite(), dir, power);
		}
	}

	private void magnetize(Direction dir, Direction moveDir, int power) {
		if (world == null)
			return;

		double magnitude = (dir == moveDir ? 1 : -1);

		double particleMotion = 0.05 * magnitude;
		double particleChance = 0.2;
		double xOff = dir.getXOffset() * particleMotion;
		double yOff = dir.getYOffset() * particleMotion;
		double zOff = dir.getZOffset() * particleMotion;

		for(int i = 1; i <= power; i++) {
			BlockPos targetPos = pos.offset(dir, i);
			BlockState targetState = world.getBlockState(targetPos);

			if (targetState.getBlock() == MagnetsModule.magnetized_block)
				break;

			if(!world.isRemote && targetState.getBlock() != Blocks.MOVING_PISTON && targetState.getBlock() != MagnetsModule.magnetized_block) {
				PushReaction reaction = MagnetSystem.getPushAction(this, targetPos, targetState, moveDir);
				if (reaction == PushReaction.IGNORE || reaction == PushReaction.DESTROY) {
					MagnetSystem.applyForce(world, targetPos, power - i + 1, dir == moveDir, moveDir, i, pos);
				}
			}

			if(targetState.isSolidSide(world, targetPos, dir) || targetState.isSolidSide(world, targetPos, dir.getOpposite()))
				break;

			if (world.isRemote && Math.random() <= particleChance) {
				double x = targetPos.getX() + (xOff == 0 ? 0.5 : Math.random());
				double y = targetPos.getY() + (yOff == 0 ? 0.5 : Math.random());
				double z = targetPos.getZ() + (zOff == 0 ? 0.5 : Math.random());
				world.addParticle(ParticleTypes.SNEEZE, x, y, z, xOff, yOff, zOff);
			}
		}
	}

	private int getPower(Direction curr) {
		if (world == null)
			return 0;

		int power = 0;
		Direction opp = curr.getOpposite();
		
		for(Direction dir : Direction.values()) {
			if(dir != opp && dir != curr) {
				int offPower = world.getRedstonePower(pos.offset(dir), dir);
				power = Math.max(offPower, power);
			}
		}
		
		return power;
	}

}
