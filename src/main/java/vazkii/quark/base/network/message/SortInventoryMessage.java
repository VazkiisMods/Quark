package vazkii.quark.base.network.message;

import vazkii.quark.base.handler.SortingHandler;
import vazkii.zeta.network.IZetaMessage;
import vazkii.zeta.network.IZetaNetworkEventContext;

import java.io.Serial;

public class SortInventoryMessage implements IZetaMessage {

	@Serial
	private static final long serialVersionUID = -4340505435110793951L;

	public boolean forcePlayer;

	public SortInventoryMessage() {}

	public SortInventoryMessage(boolean forcePlayer) {
		this.forcePlayer = forcePlayer;
	}

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		context.enqueueWork(() -> SortingHandler.sortInventory(context.getSender(), forcePlayer));
		return true;
	}

}
