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

		wireEvents();
	}

	protected final Zeta z;
	public final ZetaEventBus<IZetaLoadEvent> loadBus;
	public final ZetaEventBus<IZetaPlayEvent> playBus;

	public abstract void wireEvents();
}
