package vazkii.quark.base.network.message.structural;

import vazkii.quark.base.module.sync.SyncedFlagHandler;
import vazkii.zeta.network.IZetaMessage;
import vazkii.zeta.network.IZetaNetworkEventContext;

import java.io.Serial;
import java.util.BitSet;

public class S2CUpdateFlag implements IZetaMessage {

	@Serial
	private static final long serialVersionUID = 5889504104199410797L;

	public BitSet flags;
	public int expectedLength;
	public int expectedHash;

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		if(expectedLength == SyncedFlagHandler.expectedLength() && expectedHash == SyncedFlagHandler.expectedHash())
			SyncedFlagHandler.receiveFlagInfoFromServer(flags);
		return true;
	}

	public S2CUpdateFlag() {
		// NO-OP
	}

	private S2CUpdateFlag(BitSet flags, int expectedLength, int expectedHash) {
		this.flags = flags;
		this.expectedLength = expectedLength;
		this.expectedHash = expectedHash;
	}

	public static S2CUpdateFlag createPacket() {
		return new S2CUpdateFlag(SyncedFlagHandler.compileFlagInfo(), SyncedFlagHandler.expectedLength(), SyncedFlagHandler.expectedHash());
	}
}
