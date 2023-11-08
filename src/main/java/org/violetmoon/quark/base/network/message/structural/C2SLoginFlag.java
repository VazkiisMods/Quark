package org.violetmoon.quark.base.network.message.structural;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.BitSet;
import java.util.function.BiConsumer;

import org.violetmoon.quark.base.config.SyncedFlagHandler;

public class C2SLoginFlag extends HandshakeMessage {

	public BitSet flags;
	public int expectedLength;
	public int expectedHash;

	public C2SLoginFlag() {
		flags = SyncedFlagHandler.compileFlagInfo();
		expectedLength = SyncedFlagHandler.expectedLength();
		expectedHash = SyncedFlagHandler.expectedHash();
	}

	public C2SLoginFlag(FriendlyByteBuf buf) {
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

	@Override
	public boolean consume(NetworkEvent.Context context, BiConsumer<HandshakeMessage, NetworkEvent.Context> reply) {
		if (expectedLength == SyncedFlagHandler.expectedLength() && expectedHash == SyncedFlagHandler.expectedHash())
			SyncedFlagHandler.receiveFlagInfoFromPlayer(context.getSender(), flags);
		return true;
	}

}
