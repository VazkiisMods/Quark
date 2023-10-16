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
package vazkii.arl.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;
import vazkii.quark.base.Quark;
import vazkii.zeta.network.IZetaMessage;
import vazkii.zeta.network.ZetaMessageSerializer;
import vazkii.zeta.network.ZetaNetworkHandler;
import vazkii.zetaimplforge.network.ForgeZetaNetworkHandler;

@Deprecated(since = "zeta")
public class NetworkHandler {
	private final ZetaNetworkHandler real;

	@Deprecated(since = "zeta") //leaky abstraction - need to beef up the networking capabilities of ZetaNetworkHandler
	public final SimpleChannel channel;

	public NetworkHandler(String modid, int protocol) {
		real = Quark.ZETA.createNetworkHandler(modid, protocol);
		channel = ((ForgeZetaNetworkHandler) real).channel;
	}
	
	public <T extends IZetaMessage> void register(Class<T> clazz, NetworkDirection dir) {
		real.register(clazz, ForgeZetaNetworkHandler.fromForge(dir));
	}

	public ZetaMessageSerializer getSerializer() {
		return real.getSerializer();
	}

	public void sendToPlayer(IZetaMessage msg, ServerPlayer player) {
		real.sendToPlayer(msg, player);
	}

	public void sendToServer(IZetaMessage msg) {
		real.sendToServer(msg);
	}

	public void sendToPlayers(IZetaMessage msg, Iterable<ServerPlayer> players) {
		real.sendToPlayers(msg, players);
	}

	public void sendToAllPlayers(IMessage msg, MinecraftServer server) {
		sendToPlayers(msg, server.getPlayerList().getPlayers());
	}
}
