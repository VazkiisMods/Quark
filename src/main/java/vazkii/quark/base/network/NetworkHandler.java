/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: http://psi.vazkii.us/license.php
 *
 * File Created @ [11/01/2016, 21:58:25 (GMT)]
 */
package vazkii.quark.base.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.network.message.MessageDoEmote;
import vazkii.quark.base.network.message.MessageDropoff;
import vazkii.quark.base.network.message.MessageFavoriteItem;
import vazkii.quark.base.network.message.MessageRestock;
import vazkii.quark.base.network.message.MessageSwapItems;

public class NetworkHandler {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(LibMisc.MOD_ID);

	private static int i = 0;

	public static void init() {
		register(MessageDoEmote.class, Side.CLIENT);
		register(MessageDropoff.class, Side.SERVER);
		register(MessageSwapItems.class, Side.SERVER);
		register(MessageRestock.class, Side.SERVER);
		register(MessageFavoriteItem.class, Side.SERVER);
	}

	private static void register(Class clazz, Side handlerSide) {
		INSTANCE.registerMessage(clazz, clazz, i++, handlerSide);
	}

}
