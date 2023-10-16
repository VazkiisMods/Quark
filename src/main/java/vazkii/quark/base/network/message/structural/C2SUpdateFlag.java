package vazkii.quark.base.network.message.structural;

import vazkii.quark.base.module.sync.SyncedFlagHandler;
import vazkii.zeta.network.IZetaMessage;
import vazkii.zeta.network.IZetaNetworkEventContext;

import java.io.Serial;
import java.util.BitSet;

public class C2SUpdateFlag implements IZetaMessage {

	@Serial
	private static final long serialVersionUID = 5243197411999379903L;

	public BitSet flags;
	public int expectedLength;
	public int expectedHash;

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		if(expectedLength == SyncedFlagHandler.expectedLength() && expectedHash == SyncedFlagHandler.expectedHash())
			SyncedFlagHandler.receiveFlagInfoFromPlayer(context.getSender(), flags);
		return true;
	}

	public C2SUpdateFlag() {
		// NO-OP
	}

	private C2SUpdateFlag(BitSet flags, int expectedLength, int expectedHash) {
		this.flags = flags;
		this.expectedLength = expectedLength;
		this.expectedHash = expectedHash;
	}

	public static C2SUpdateFlag createPacket() {
		return new C2SUpdateFlag(SyncedFlagHandler.compileFlagInfo(), SyncedFlagHandler.expectedLength(), SyncedFlagHandler.expectedHash());
	}
}
