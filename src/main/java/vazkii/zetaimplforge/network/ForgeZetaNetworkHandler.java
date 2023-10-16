package vazkii.zetaimplforge.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.VisibleForTesting;
import vazkii.zeta.network.IZetaMessage;
import vazkii.zeta.network.ZetaNetworkDirection;
import vazkii.zeta.network.ZetaNetworkHandler;

public class ForgeZetaNetworkHandler extends ZetaNetworkHandler {
	@VisibleForTesting //TODO ZETA: encapsulate, leaky abstraction in QuarkNetwork
	public final SimpleChannel channel;
	private int i = 0;

	public ForgeZetaNetworkHandler(String modid, int protocolVersion) {
		super(modid, protocolVersion);

		String protocolStr = Integer.toString(protocolVersion);

		channel = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(modid, "main"))
			.networkProtocolVersion(() -> protocolStr)
			.clientAcceptedVersions(protocolStr::equals)
			.serverAcceptedVersions(protocolStr::equals)
			.simpleChannel();
	}

	@Override
	public <T extends IZetaMessage> void register(Class<T> clazz, ZetaNetworkDirection dir) {
		BiConsumer<T, FriendlyByteBuf> encoder = serializer::writeObject;
		NetworkDirection forgeDir = toForge(dir);

		Function<FriendlyByteBuf, T> decoder = (buf) -> {
			try {
				T msg = clazz.getDeclaredConstructor().newInstance();
				serializer.readObject(msg, buf);
				return msg;
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		};

		BiConsumer<T, Supplier<NetworkEvent.Context>> consumer = (msg, supp) -> {
			NetworkEvent.Context context = supp.get();
			if(context.getDirection() != forgeDir)
				return;

			context.setPacketHandled(msg.receive(new ForgeNetworkEventContextImpl(context)));
		};

		channel.registerMessage(i, clazz, encoder, decoder, consumer);
		i++;
	}

	@Override
	public void sendToPlayer(IZetaMessage msg, ServerPlayer player) {
		channel.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}

	@Override
	public void sendToServer(IZetaMessage msg) {
		channel.sendToServer(msg);
	}

	///

	public static ZetaNetworkDirection fromForge(NetworkDirection dir) {
		return switch(dir) {
			case PLAY_TO_SERVER -> ZetaNetworkDirection.PLAY_TO_SERVER;
			case PLAY_TO_CLIENT -> ZetaNetworkDirection.PLAY_TO_CLIENT;
			default -> throw new UnsupportedOperationException("unknown net direction " + dir);
		};
	}

	public static NetworkDirection toForge(ZetaNetworkDirection dir) {
		return switch(dir) {
			case PLAY_TO_SERVER -> NetworkDirection.PLAY_TO_SERVER;
			case PLAY_TO_CLIENT -> NetworkDirection.PLAY_TO_CLIENT;
		};
	}
}
