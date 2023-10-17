package vazkii.zeta;

import org.apache.logging.log4j.Logger;
import vazkii.zeta.event.ZetaEventBus;
import vazkii.zeta.module.ZetaModuleManager;
import vazkii.zeta.network.ZetaNetworkHandler;
import vazkii.zeta.registry.ZetaRegistry;
import vazkii.zeta.util.ZetaSide;

/**
 * do not touch forge OR quark from this package, it will later be split off
 */
public abstract class Zeta {
	public Zeta(Logger log) {
		this.log = log;

		this.eventBus = new ZetaEventBus();
		this.modules = new ZetaModuleManager(this, eventBus);
	}

	public final Logger log;
	public final ZetaEventBus eventBus;
	public final ZetaModuleManager modules;

	public abstract ZetaSide getSide();
	public abstract boolean isModLoaded(String modid);

	public abstract ZetaRegistry createRegistry(String modid);
	public abstract ZetaNetworkHandler createNetworkHandler(String modid, int protocolVersion);
}
