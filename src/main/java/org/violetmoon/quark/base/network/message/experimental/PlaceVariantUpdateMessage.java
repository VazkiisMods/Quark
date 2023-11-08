package org.violetmoon.quark.base.network.message.experimental;

import org.violetmoon.quark.content.experimental.module.VariantSelectorModule;
import org.violetmoon.zeta.network.IZetaMessage;
import org.violetmoon.zeta.network.IZetaNetworkEventContext;

import java.io.Serial;

public class PlaceVariantUpdateMessage implements IZetaMessage {

	@Serial
	private static final long serialVersionUID = -6123685825175210844L;

	public String variant;

	public PlaceVariantUpdateMessage() {}

	public PlaceVariantUpdateMessage(String variant) {
		this.variant = variant;
	}

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		context.enqueueWork(() -> {
			VariantSelectorModule.setSavedVariant(context.getSender(), variant);
		});
		return true;
	}

}
