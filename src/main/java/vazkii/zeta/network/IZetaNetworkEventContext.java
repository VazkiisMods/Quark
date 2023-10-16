package vazkii.zeta.network;

import java.util.concurrent.CompletableFuture;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public interface IZetaNetworkEventContext {
	CompletableFuture<Void> enqueueWork(Runnable runnable);
	@Nullable ServerPlayer getSender();
	Connection getNetworkManager();
}
