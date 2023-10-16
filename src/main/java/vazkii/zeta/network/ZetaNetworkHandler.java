package vazkii.zeta.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public abstract class ZetaNetworkHandler {
	public ZetaMessageSerializer serializer = new ZetaMessageSerializer();

	protected final String modid;
	protected final int protocolVersion;

	public ZetaNetworkHandler(String modid, int protocolVersion) {
		this.modid = modid;
		this.protocolVersion = protocolVersion;
	}

	public ZetaMessageSerializer getSerializer() {
		return serializer;
	}

	public void sendToPlayers(IZetaMessage msg, Iterable<ServerPlayer> players) {
		for(ServerPlayer player : players) sendToPlayer(msg, player);
	}

	public void sendToAllPlayers(IZetaMessage msg, MinecraftServer server) {
		sendToPlayers(msg, server.getPlayerList().getPlayers());
	}

	public abstract <T extends IZetaMessage> void register(Class<T> clazz, ZetaNetworkDirection dir);

	public abstract void sendToPlayer(IZetaMessage msg, ServerPlayer player);
	public abstract void sendToServer(IZetaMessage msg);
}
