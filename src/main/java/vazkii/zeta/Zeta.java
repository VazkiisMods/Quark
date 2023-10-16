package vazkii.zeta;

import vazkii.zeta.network.ZetaNetworkHandler;

/**
 * do not touch forge from this package, it will later be split off
 */
public abstract class Zeta {
	public abstract ZetaNetworkHandler createNetworkHandler(String modid, int protocolVersion);
}
