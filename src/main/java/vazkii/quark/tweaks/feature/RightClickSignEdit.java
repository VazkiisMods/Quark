/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/03/2016, 00:21:42 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibGuiIDs;
import vazkii.quark.base.lib.LibObfuscation;
import vazkii.quark.base.module.Feature;

public class RightClickSignEdit extends Feature {

	boolean emptyHand;

	@Override
	public void setupConfig() {
		emptyHand = loadPropBool("Requires Empty Hands", "", false);
	}

	@SubscribeEvent
	public void onInteract(PlayerInteractEvent.RightClickBlock event) {
		if(event.getUseBlock() == Result.DENY)
			return;	

		TileEntity tile = event.getWorld().getTileEntity(event.getPos());
		if(tile instanceof TileEntitySign && (!emptyHand || event.getEntityPlayer().getHeldItemMainhand().isEmpty()) && event.getEntityPlayer().capabilities.allowEdit && !event.getEntity().isSneaking()) {
			IBlockState state = event.getWorld().getBlockState(event.getPos());
			if(state.getBlock().getRegistryName().toString().contains("tcguideposts"))
				return;
			
			TileEntitySign sign = (TileEntitySign) tile;
			sign.setPlayer(event.getEntityPlayer());
			ReflectionHelper.setPrivateValue(TileEntitySign.class, sign, true, LibObfuscation.IS_EDITABLE);

			event.getEntityPlayer().openGui(Quark.instance, LibGuiIDs.SIGN, event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
