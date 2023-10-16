package vazkii.quark.base.network.message.experimental;

import java.io.Serial;

import net.minecraftforge.network.NetworkEvent;
import vazkii.arl.network.IMessage;
import vazkii.quark.content.experimental.module.VariantSelectorModule;
import vazkii.zeta.network.IZetaMessage;
import vazkii.zeta.network.IZetaNetworkEventContext;

public class PlaceVariantUpdateMessage implements IZetaMessage {

	@Serial
	private static final long serialVersionUID = -6123685825175210844L;

	public String variant;
	
	public PlaceVariantUpdateMessage() { }

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
