/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [24/03/2016, 04:28:00 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class KnockOnDoors extends Feature {

	@SubscribeEvent
	public void leftClick(PlayerInteractEvent.LeftClickBlock event) {
		if(event.getEntityPlayer().getHeldItemMainhand() == null) {
			IBlockState state = event.getWorld().getBlockState(event.getPos());
			Block block = state.getBlock();
			if(block instanceof BlockDoor && state.getMaterial() == Material.wood)
				event.getWorld().playSound(null, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), block.getStepSound().getPlaceSound(), SoundCategory.PLAYERS, 1F, 1F);
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
