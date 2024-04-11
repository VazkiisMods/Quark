package org.violetmoon.quark.base.network.message;

import org.violetmoon.quark.base.handler.InventoryTransferHandler;
import org.violetmoon.zeta.network.IZetaMessage;
import org.violetmoon.zeta.network.IZetaNetworkEventContext;

import java.io.Serial;

public class InventoryTransferMessage implements IZetaMessage {

	@Serial
	private static final long serialVersionUID = 3825599549474465007L;

	public boolean smart, restock;

	public InventoryTransferMessage() {}

	public InventoryTransferMessage(boolean smart, boolean restock) {
		this.smart = smart;
		this.restock = restock;
	}

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		context.enqueueWork(() -> InventoryTransferHandler.transfer(context.getSender(), restock, smart));
		return true;
	}

}
