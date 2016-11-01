/**
 * This class was created by <Palaster>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [01/11/2016, 17:26:00 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class DoubleDoors extends Feature {

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
		if(event.getEntityPlayer().isSneaking() || event.getWorld().isRemote)
			return;

		IBlockState state = event.getWorld().getBlockState(event.getPos()).getActualState(event.getWorld(), event.getPos());
		Block block = state.getBlock();

		if(!(block instanceof BlockDoor))
			return;

		EnumFacing direction = state.getValue(BlockDoor.FACING);
		boolean isOpen = state.getValue(BlockDoor.OPEN);
		BlockDoor.EnumHingePosition isMirrored = state.getValue(BlockDoor.HINGE);

		BlockPos pos = event.getPos().offset(isMirrored == BlockDoor.EnumHingePosition.RIGHT ? direction.rotateYCCW() : direction.rotateY());
		IBlockState other = event.getWorld().getBlockState(pos).getActualState(event.getWorld(), pos);

		if(other.getBlock() == (BlockDoor) block && other.getValue(BlockDoor.FACING) == direction && other.getValue(BlockDoor.OPEN) == isOpen && other.getValue(BlockDoor.HINGE) != isMirrored)
			((BlockDoor) block).onBlockActivated(event.getWorld(), pos, other, event.getEntityPlayer(), event.getHand(), event.getItemStack(), event.getFace(), 0, 0, 0);
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
