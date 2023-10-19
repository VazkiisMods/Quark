package vazkii.quark.base.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import vazkii.quark.base.Quark;
import vazkii.quark.base.network.message.*;
import vazkii.quark.base.network.message.experimental.PlaceVariantUpdateMessage;
import vazkii.quark.base.network.message.oddities.HandleBackpackMessage;
import vazkii.quark.base.network.message.oddities.MatrixEnchanterOperationMessage;
import vazkii.quark.base.network.message.oddities.ScrollCrateMessage;
import vazkii.quark.base.network.message.structural.*;
import vazkii.quark.content.tweaks.module.LockRotationModule;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.network.IZetaMessage;
import vazkii.zeta.network.ZetaNetworkDirection;
import vazkii.zeta.network.ZetaNetworkHandler;
import vazkii.zetaimplforge.network.ForgeZetaNetworkHandler;

import java.time.Instant;
import java.util.BitSet;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class QuarkNetwork {

	private static final int PROTOCOL_VERSION = 2;

	private static ZetaNetworkHandler network;

	@Deprecated(forRemoval = true)
	private static SimpleChannel channel;

	@LoadEvent
	public static void setup(ZCommonSetup event) {
		network = Quark.ZETA.createNetworkHandler(Quark.MOD_ID, PROTOCOL_VERSION);
		channel = ((ForgeZetaNetworkHandler) network).channel; //TODO: LEAKY ABSTRACTION

		network.getSerializer().mapHandlers(Instant.class, (buf, field) -> buf.readInstant(), (buf, field, instant) -> buf.writeInstant(instant));
		network.getSerializer().mapHandlers(MessageSignature.class, (buf, field) -> new MessageSignature(buf), (buf, field, signature) -> signature.write(buf));
		network.getSerializer().mapHandlers(LastSeenMessages.Update.class, (buf, field) -> new LastSeenMessages.Update(buf), (buf, field, update) -> update.write(buf));
		network.getSerializer().mapHandlers(BitSet.class, (buf, field) -> BitSet.valueOf(buf.readLongArray()), (buf, field, bitSet) -> buf.writeLongArray(bitSet.toLongArray()));

		// Base Quark
		network.register(SortInventoryMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(InventoryTransferMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(DoubleDoorMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(HarvestMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(RequestEmoteMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(ChangeHotbarMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(SetLockProfileMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(ShareItemMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(ScrollOnBundleMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.getSerializer().mapHandlers(LockRotationModule.LockProfile.class, LockRotationModule.LockProfile::readProfile, LockRotationModule.LockProfile::writeProfile);

		// Oddities
		network.register(HandleBackpackMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(MatrixEnchanterOperationMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(ScrollCrateMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);

		// Experimental
		network.register(PlaceVariantUpdateMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);

		// Clientbound
		network.register(DoEmoteMessage.class, ZetaNetworkDirection.PLAY_TO_CLIENT);
		network.register(EditSignMessage.class, ZetaNetworkDirection.PLAY_TO_CLIENT);
		network.register(UpdateTridentMessage.class, ZetaNetworkDirection.PLAY_TO_CLIENT);

		// Flag Syncing
		network.register(S2CUpdateFlag.class, ZetaNetworkDirection.PLAY_TO_CLIENT);
		network.register(C2SUpdateFlag.class, ZetaNetworkDirection.PLAY_TO_SERVER);


		loginIndexedBuilder(S2CLoginFlag.class, 98, NetworkDirection.LOGIN_TO_CLIENT)
				.decoder(S2CLoginFlag::new)
				.consumerNetworkThread(loginPacketHandler())
				.buildLoginPacketList(S2CLoginFlag::generateRegistryPackets)
				.add();
		loginIndexedBuilder(C2SLoginFlag.class, 99, NetworkDirection.LOGIN_TO_SERVER)
				.decoder(C2SLoginFlag::new)
				.consumerNetworkThread(loginIndexFirst(loginPacketHandler()))
				.noResponse()
				.add();
	}

	private static <MSG extends HandshakeMessage> SimpleChannel.MessageBuilder<MSG> loginIndexedBuilder(Class<MSG> clazz, int id, NetworkDirection direction) {
		return channel.messageBuilder(clazz, id, direction)
				.loginIndex(HandshakeMessage::getLoginIndex, HandshakeMessage::setLoginIndex)
				.encoder(HandshakeMessage::encode);
	}

	private static <MSG extends HandshakeMessage> BiConsumer<MSG, Supplier<NetworkEvent.Context>> loginPacketHandler() {
		return (msg, contextSupplier) -> {
			NetworkEvent.Context context = contextSupplier.get();
			context.setPacketHandled(msg.consume(context, channel::reply));
		};
	}

	private static <MSG extends HandshakeMessage> BiConsumer<MSG, Supplier<NetworkEvent.Context>> loginIndexFirst(BiConsumer<MSG, Supplier<NetworkEvent.Context>> toWrap) {
		return HandshakeHandler.indexFirst((handler, msg, context) -> toWrap.accept(msg, context));
	}

	public static void sendToPlayer(IZetaMessage msg, ServerPlayer player) {
		if(network == null)
			return;

		network.sendToPlayer(msg, player);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendToServer(IZetaMessage msg) {
		if(network == null || Minecraft.getInstance().getConnection() == null)
			return;

		network.sendToServer(msg);
	}

	public static void sendToPlayers(IZetaMessage msg, Iterable<ServerPlayer> players) {
		if(network == null)
			return;

		network.sendToPlayers(msg, players);
	}

	public static void sendToAllPlayers(IZetaMessage msg, MinecraftServer server) {
		if(network == null)
			return;

		network.sendToAllPlayers(msg, server);
	}

	public static Packet<?> toVanillaPacket(IZetaMessage msg, NetworkDirection direction) {
		return channel.toVanillaPacket(msg, direction);
	}

}
