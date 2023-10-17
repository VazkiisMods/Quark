package vazkii.zetaimplforge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.Logger;
import vazkii.zeta.Zeta;
import vazkii.zeta.network.ZetaNetworkHandler;
import vazkii.zeta.registry.ZetaRegistry;
import vazkii.zeta.util.ZetaSide;
import vazkii.zetaimplforge.network.ForgeZetaNetworkHandler;
import vazkii.zetaimplforge.registry.ForgeZetaRegistry;

/**
 * ideally do not touch quark from this package, it will later be split off
 */
public class ForgeZeta extends Zeta {
	public ForgeZeta(Logger log) {
		super(log);

		this.eventPassage = new ForgeEventPassage(this);
	}

	private final ForgeEventPassage eventPassage;

	@Override
	public ZetaSide getSide() {
		return switch(FMLEnvironment.dist) {
			case CLIENT -> ZetaSide.CLIENT;
			case DEDICATED_SERVER -> ZetaSide.SERVER;
		};
	}

	@Override
	public boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}

	@Override
	public ZetaRegistry createRegistry(String modid) {
		return new ForgeZetaRegistry(this, modid);
	}

	@Override
	public ZetaNetworkHandler createNetworkHandler(String modid, int protocolVersion) {
		return new ForgeZetaNetworkHandler(modid, protocolVersion);
	}
}
