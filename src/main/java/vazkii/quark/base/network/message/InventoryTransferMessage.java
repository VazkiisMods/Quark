package vazkii.quark.base.network.message;

import vazkii.quark.base.handler.InventoryTransferHandler;
import vazkii.zeta.network.IZetaMessage;
import vazkii.zeta.network.IZetaNetworkEventContext;

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
