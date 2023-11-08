package org.violetmoon.zeta.network;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface IZetaNetworkEventContext {
	CompletableFuture<Void> enqueueWork(Runnable runnable);
	@Nullable ServerPlayer getSender();
	Connection getNetworkManager();
}
