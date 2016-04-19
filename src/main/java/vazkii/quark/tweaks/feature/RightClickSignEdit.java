/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [19/03/2016, 00:21:42 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibGuiIDs;
import vazkii.quark.base.module.Feature;

public class RightClickSignEdit extends Feature {

	boolean emptyHand;

	@Override
	public void setupConfig() {
		emptyHand = loadPropBool("Requires Empty Hands", "", false);
	}

	@SubscribeEvent
	public void onInteract(PlayerInteractEvent.RightClickBlock event) {
		TileEntity tile = event.getWorld().getTileEntity(event.getPos());
		if(tile instanceof TileEntitySign && (!emptyHand || event.getEntityPlayer().getHeldItemMainhand() == null))
			event.getEntityPlayer().openGui(Quark.instance, LibGuiIDs.SIGN, event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
