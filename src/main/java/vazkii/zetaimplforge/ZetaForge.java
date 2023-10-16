package vazkii.zetaimplforge;

import vazkii.zeta.Zeta;
import vazkii.zeta.network.ZetaNetworkHandler;
import vazkii.zeta.registry.ZetaRegistry;
import vazkii.zetaimplforge.network.ForgeZetaNetworkHandler;
import vazkii.zetaimplforge.registry.ForgeZetaRegistry;

public class ZetaForge extends Zeta {
	@Override
	public ZetaRegistry createRegistry(String modid) {
		return new ForgeZetaRegistry(modid);
	}

	@Override
	public ZetaNetworkHandler createNetworkHandler(String modid, int protocolVersion) {
		return new ForgeZetaNetworkHandler(modid, protocolVersion);
	}
}
