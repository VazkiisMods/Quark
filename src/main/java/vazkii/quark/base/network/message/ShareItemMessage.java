package vazkii.quark.base.network.message;

import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import vazkii.quark.content.management.module.ItemSharingModule;
import vazkii.zeta.network.IZetaMessage;
import vazkii.zeta.network.IZetaNetworkEventContext;

import java.io.Serial;
import java.time.Instant;

public class ShareItemMessage implements IZetaMessage {

	@Serial
	private static final long serialVersionUID = 3550769853533388357L;

	public ItemStack stack;
	public String message;
	public Instant timeStamp;
	public long salt;
	public MessageSignature signature;
	public boolean signedPreview;
	public LastSeenMessages.Update lastSeenMessages;

	public ShareItemMessage() {}

	public ShareItemMessage(ItemStack stack, String message, Instant timeStamp, long salt, MessageSignature signature, boolean signedPreview, LastSeenMessages.Update lastSeenMessages) {
		this.stack = stack;
		this.message = message;
		this.timeStamp = timeStamp;
		this.salt = salt;
		this.signature = signature;
		this.signedPreview = signedPreview;
		this.lastSeenMessages = lastSeenMessages;
	}

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		ServerPlayer player = context.getSender();
		if(player != null && player.server != null)
			context.enqueueWork(() -> ItemSharingModule.shareItem(player, message, stack, timeStamp, salt, signature, signedPreview, lastSeenMessages));

		return true;
	}

}
