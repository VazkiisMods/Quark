package vazkii.zeta.network;

import java.io.Serializable;

public interface IZetaMessage extends Serializable {
	//TODO ZETA: rename to just "receive"
	boolean receiveZ(IZetaNetworkEventContext context);
}
