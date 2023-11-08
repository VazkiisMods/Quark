package org.violetmoon.quark.base.network.message.structural;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.violetmoon.quark.base.config.SyncedFlagHandler;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class S2CLoginFlag extends HandshakeMessage {

	public BitSet flags;
	public int expectedLength;
	public int expectedHash;

	public S2CLoginFlag() {
		flags = SyncedFlagHandler.compileFlagInfo();
		expectedLength = SyncedFlagHandler.expectedLength();
		expectedHash = SyncedFlagHandler.expectedHash();
	}

	public S2CLoginFlag(FriendlyByteBuf buf) {
		this.flags = BitSet.valueOf(buf.readLongArray());
		this.expectedLength = buf.readInt();
		this.expectedHash = buf.readInt();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeLongArray(flags.toLongArray());
		buf.writeInt(expectedLength);
		buf.writeInt(expectedHash);
	}

	public static List<Pair<String, S2CLoginFlag>> generateRegistryPackets(boolean isLocal) {
		return !isLocal ?
			Collections.singletonList(Pair.of(S2CLoginFlag.class.getName(), new S2CLoginFlag())) :
			Collections.emptyList();
	}

	@Override
	public boolean consume(NetworkEvent.Context context, BiConsumer<HandshakeMessage, NetworkEvent.Context> reply) {
		if (expectedLength == SyncedFlagHandler.expectedLength() && expectedHash == SyncedFlagHandler.expectedHash())
			SyncedFlagHandler.receiveFlagInfoFromServer(flags);
		reply.accept(new C2SLoginFlag(), context);
		return true;
	}
}
