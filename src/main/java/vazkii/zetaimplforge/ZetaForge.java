package vazkii.zetaimplforge;

import vazkii.zeta.Zeta;
import vazkii.zeta.network.ZetaNetworkHandler;
import vazkii.zetaimplforge.network.ForgeZetaNetworkHandler;

public class ZetaForge extends Zeta {
	@Override
	public ZetaNetworkHandler createNetworkHandler(String modid, int protocolVersion) {
		return new ForgeZetaNetworkHandler(modid, protocolVersion);
	}
}
