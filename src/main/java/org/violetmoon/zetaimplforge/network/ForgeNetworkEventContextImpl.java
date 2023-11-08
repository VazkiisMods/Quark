package org.violetmoon.zetaimplforge.network;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.network.IZetaNetworkEventContext;

import java.util.concurrent.CompletableFuture;

public class ForgeNetworkEventContextImpl implements IZetaNetworkEventContext {
	private final NetworkEvent.Context ctx;

	public ForgeNetworkEventContextImpl(NetworkEvent.Context ctx) {
		this.ctx = ctx;
	}

	@Override
	public CompletableFuture<Void> enqueueWork(Runnable runnable) {
		return ctx.enqueueWork(runnable);
	}

	@Override
	public @Nullable ServerPlayer getSender() {
		return ctx.getSender();
	}

	@Override
	public Connection getNetworkManager() {
		return ctx.getNetworkManager();
	}
}
