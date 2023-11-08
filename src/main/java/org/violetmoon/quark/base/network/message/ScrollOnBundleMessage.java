package org.violetmoon.quark.base.network.message;

import org.violetmoon.quark.content.management.module.ExpandedItemInteractionsModule;
import org.violetmoon.zeta.network.IZetaMessage;
import org.violetmoon.zeta.network.IZetaNetworkEventContext;

import java.io.Serial;

public class ScrollOnBundleMessage implements IZetaMessage {

	@Serial
	private static final long serialVersionUID = 5598418693967300303L;

	public int containerId;
	public int stateId;
	public int slotNum;
	public double scrollDelta;

	public ScrollOnBundleMessage() {}

	public ScrollOnBundleMessage(int containerId, int stateId, int slotNum, double scrollDelta) {
		this.containerId = containerId;
		this.stateId = stateId;
		this.slotNum = slotNum;
		this.scrollDelta = scrollDelta;
	}

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		context.enqueueWork(() -> ExpandedItemInteractionsModule.scrollOnBundle(context.getSender(), containerId, stateId, slotNum, scrollDelta));
		return true;
	}

}
