/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [28/03/2016, 17:05:38 (GMT)]
 */
package vazkii.quark.api;

import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.items.IItemHandler;

/**
 * Implement on a TileEntity to allow it to receive dropoff, and to have
 * chest buttons on the client. 
 */
public interface IDropoffManager {

	public boolean acceptsDropoff(EntityPlayer player);
	
	/**
	 * Override this if you want to add a custom IItemHandler for dropoff.
	 */
	public default IItemHandler getDropoffItemHandler(Supplier<IItemHandler> defaultSupplier) {
		return defaultSupplier.get();
	}
	
}
