package vazkii.zeta.network;

import net.minecraft.server.level.ServerPlayer;

public abstract class ZetaNetworkHandler {
	public ZetaMessageSerializer serializer = new ZetaMessageSerializer();

	private final String modid;
	private final int protocolVersion;

	public ZetaNetworkHandler(String modid, int protocolVersion) {
		this.modid = modid;
		this.protocolVersion = protocolVersion;
	}

	public ZetaMessageSerializer getSerializer() {
		return serializer;
	}

	public abstract <T extends IZetaMessage> void register(Class<T> clazz, ZetaNetworkDirection dir);

	public abstract void sendToPlayer(IZetaMessage msg, ServerPlayer player);
	public abstract void sendToServer(IZetaMessage msg);
}
