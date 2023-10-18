package vazkii.zeta.client;

import vazkii.zeta.Zeta;
import vazkii.zeta.event.bus.IZetaLoadEvent;
import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.ZetaEventBus;

public abstract class ZetaClient {
	public ZetaClient(Zeta z) {
		this.z = z;
		this.loadBus = z.loadBus;
		this.playBus = z.playBus;

		this.ticker = new ClientTicker();

		wireEvents();
	}

	protected final Zeta z;
	protected final ZetaEventBus<IZetaLoadEvent> loadBus;
	protected final ZetaEventBus<IZetaPlayEvent> playBus;

	public final ClientTicker ticker;

	public abstract void wireEvents();
}
