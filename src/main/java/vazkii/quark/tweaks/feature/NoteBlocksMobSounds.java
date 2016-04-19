/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [27/03/2016, 06:02:47 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class NoteBlocksMobSounds extends Feature {

	@SubscribeEvent
	public void noteBlockPlayed(NoteBlockEvent.Play event) {
		BlockPos pos = event.getPos();

		EnumFacing[] facings = new EnumFacing[] {
				EnumFacing.NORTH,
				EnumFacing.SOUTH,
				EnumFacing.EAST,
				EnumFacing.WEST
		};

		TileEntity tile = null;
		boolean can = false;
		for(EnumFacing face : facings) {
			BlockPos apos = pos.offset(face);
			tile = event.getWorld().getTileEntity(apos);
			if(tile != null && tile instanceof TileEntitySkull) {
				IBlockState state = event.getWorld().getBlockState(apos);
				if(state.getValue(BlockSkull.FACING) == face) {
					can = true;
					break;
				}
			}
		}

		if(can) {
			int type = ((TileEntitySkull) tile).getSkullType();
			if(type != 3) {
				event.setCanceled(true);

				SoundEvent sound = null;
				switch(type) {
				case 0:
				case 1:
					sound = SoundEvents.entity_skeleton_ambient;
					break;
				case 2:
					sound = SoundEvents.entity_zombie_ambient;
					break;
				case 4:
					sound = SoundEvents.entity_creeper_primed;
					break;
				case 5:
					sound = SoundEvents.entity_enderdragon_ambient;
					break;
				}

				if(sound != null) {
					System.out.println("play");
					event.getWorld().playSound(null, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, sound, SoundCategory.BLOCKS, 1F, 1F);
				}
			}
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
