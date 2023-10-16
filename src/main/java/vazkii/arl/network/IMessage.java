package vazkii.arl.network;

import java.io.Serializable;

import net.minecraftforge.network.NetworkEvent;
import vazkii.zeta.network.IZetaMessage;
import vazkii.zeta.network.IZetaNetworkEventContext;
import vazkii.zetaimplforge.network.ForgeNetworkEventContextImpl;

@Deprecated(since = "zeta")
public interface IMessage extends Serializable, IZetaMessage {

	@Deprecated(since = "zeta")
	public boolean receive(NetworkEvent.Context context);

	@Override
	default boolean receiveZ(IZetaNetworkEventContext context) {
		return receive(((ForgeNetworkEventContextImpl) context).DEPRECATED_getGuts());
	}
}