/**
 * This class was created by <Palaster>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [06/06/2018, 02:10:00 (GMT)]
 */
package vazkii.quark.tweaks.ai;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIDoorInteract;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class EntityAIOpenDoubleDoor extends EntityAIDoorInteract {
	boolean closeDoor;
	int closeDoorTemporisation;

	public EntityAIOpenDoubleDoor(EntityLiving entitylivingIn, boolean shouldClose) {
		super(entitylivingIn);
		entity = entitylivingIn;
		closeDoor = shouldClose;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return closeDoor && closeDoorTemporisation > 0 && super.shouldContinueExecuting();
	}

	@Override
	public void startExecuting() {
		closeDoorTemporisation = 20;

		IBlockState state = entity.world.getBlockState(doorPosition).getActualState(entity.world, doorPosition);

		EnumFacing direction = state.getValue(BlockDoor.FACING);
		boolean isOpen = state.getValue(BlockDoor.OPEN);
		BlockDoor.EnumHingePosition isMirrored = state.getValue(BlockDoor.HINGE);

		BlockPos mirrorPos = doorPosition.offset(isMirrored == BlockDoor.EnumHingePosition.RIGHT ? direction.rotateYCCW() : direction.rotateY());
		BlockPos doorPos = state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER ? mirrorPos : mirrorPos.down();
		IBlockState other = entity.world.getBlockState(doorPos).getActualState(entity.world, doorPos);

		if(state.getMaterial() != Material.IRON && other.getBlock() == doorBlock && other.getValue(BlockDoor.FACING) == direction && other.getValue(BlockDoor.OPEN) == isOpen && other.getValue(BlockDoor.HINGE) != isMirrored) {
			IBlockState newState = other.cycleProperty(BlockDoor.OPEN);
			entity.world.setBlockState(doorPos, newState, 10);
		}

		doorBlock.toggleDoor(entity.world, doorPosition, true);
	}

	@Override
	public void resetTask() {
		if(closeDoor) {
			IBlockState state = entity.world.getBlockState(doorPosition).getActualState(entity.world, doorPosition);

			EnumFacing direction = state.getValue(BlockDoor.FACING);
			boolean isOpen = state.getValue(BlockDoor.OPEN);
			BlockDoor.EnumHingePosition isMirrored = state.getValue(BlockDoor.HINGE);

			BlockPos mirrorPos = doorPosition.offset(isMirrored == BlockDoor.EnumHingePosition.RIGHT ? direction.rotateYCCW() : direction.rotateY());
			BlockPos doorPos = state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER ? mirrorPos : mirrorPos.down();
			IBlockState other = entity.world.getBlockState(doorPos).getActualState(entity.world, doorPos);

			if(state.getMaterial() != Material.IRON && other.getBlock() == doorBlock && other.getValue(BlockDoor.FACING) == direction && other.getValue(BlockDoor.OPEN) == isOpen && other.getValue(BlockDoor.HINGE) != isMirrored) {
				IBlockState newState = other.cycleProperty(BlockDoor.OPEN);
				entity.world.setBlockState(doorPos, newState, 10);
			}

			doorBlock.toggleDoor(entity.world, doorPosition, false);
		}
	}

	@Override
	public void updateTask() {
		--closeDoorTemporisation;
		super.updateTask();
	}
}