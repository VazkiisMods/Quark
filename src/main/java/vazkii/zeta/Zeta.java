package vazkii.zeta;

import vazkii.zeta.network.ZetaNetworkHandler;
import vazkii.zeta.registry.ZetaRegistry;

/**
 * do not touch forge from this package, it will later be split off
 */
public abstract class Zeta {
	public abstract ZetaRegistry createRegistry(String modid);
	public abstract ZetaNetworkHandler createNetworkHandler(String modid, int protocolVersion);
}
