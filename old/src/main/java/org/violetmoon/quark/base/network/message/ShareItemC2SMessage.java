package org.violetmoon.quark.base.network.message;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.management.module.ItemSharingModule;
import org.violetmoon.zeta.network.IZetaMessage;
import org.violetmoon.zeta.network.IZetaNetworkEventContext;

// The client, requesting "hey I'd like to share this item"
public class ShareItemC2SMessage implements IZetaMessage {
	public ItemStack toShare;

	public ShareItemC2SMessage() {

	}

	public ShareItemC2SMessage(ItemStack stack) {
		this.toShare = stack;
	}

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		ServerPlayer sender = context.getSender();
		context.enqueueWork(() -> {
			if(sender == null)
				return;

			MinecraftServer server = sender.getServer();
			if(server == null)
				return;

			if(!ItemSharingModule.canShare(sender.getUUID(), server))
				return;

			Component senderName = sender.getDisplayName();

			Quark.ZETA.network.sendToAllPlayers(new ShareItemS2CMessage(sender.getUUID(), senderName, toShare), server);
		});

		return true;
	}
}
