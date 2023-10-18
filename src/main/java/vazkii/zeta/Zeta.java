package vazkii.zeta;

import org.apache.logging.log4j.Logger;
import vazkii.zeta.client.ClientTicker;
import vazkii.zeta.event.bus.IZetaLoadEvent;
import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.ZetaEventBus;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaModuleManager;
import vazkii.zeta.network.ZetaNetworkHandler;
import vazkii.zeta.registry.ZetaRegistry;
import vazkii.zeta.util.ZetaSide;

/**
 * do not touch forge OR quark from this package, it will later be split off
 */
public abstract class Zeta {
	public Zeta(String modid, Logger log) {
		this.log = log;

		this.side = getSide();
		this.loadBus = new ZetaEventBus<>(LoadEvent.class, IZetaLoadEvent.class);
		this.playBus = new ZetaEventBus<>(PlayEvent.class, IZetaPlayEvent.class);
		this.modules = new ZetaModuleManager(this);
		this.registry = createRegistry(modid);

		this.ticker_SHOULD_NOT_BE_HERE = new ClientTicker();

		wireEvents();
	}

	public final Logger log;

	public final ZetaSide side;
	public final ZetaEventBus<IZetaLoadEvent> loadBus;
	public final ZetaEventBus<IZetaPlayEvent> playBus;
	public final ZetaModuleManager modules;
	public final ZetaRegistry registry;

	//TODO: move to ZetaClient. Some bits of the server *do* actually need this for some raisin (config code)
	@Deprecated public final ClientTicker ticker_SHOULD_NOT_BE_HERE;

	public abstract ZetaSide getSide();
	public abstract boolean isModLoaded(String modid);

	public abstract ZetaRegistry createRegistry(String modid);
	public abstract ZetaNetworkHandler createNetworkHandler(String modid, int protocolVersion);
	public abstract void wireEvents();
}
