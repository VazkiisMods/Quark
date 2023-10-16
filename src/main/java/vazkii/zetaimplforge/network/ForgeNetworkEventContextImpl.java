package vazkii.zetaimplforge.network;

import java.util.concurrent.CompletableFuture;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;
import vazkii.zeta.network.IZetaNetworkEventContext;

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

	//TODO ZETA: remove and actually *use* this API
	@Deprecated(forRemoval = true)
	public NetworkEvent.Context DEPRECATED_getGuts() {
		return ctx;
	}
}
