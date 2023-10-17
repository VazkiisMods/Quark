package vazkii.quark.base;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import vazkii.quark.base.proxy.ClientProxy;
import vazkii.quark.base.proxy.CommonProxy;
import vazkii.zeta.Zeta;
import vazkii.zeta.registry.ZetaRegistry;
import vazkii.zetaimplforge.ForgeZeta;

@Mod(Quark.MOD_ID)
public class Quark {

	public static final String MOD_ID = "quark";
	public static final String ODDITIES_ID = "quarkoddities";

	public static Quark instance;
	public static CommonProxy proxy;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);

	public static final Zeta ZETA = new ForgeZeta(LogManager.getLogger("quark-zeta"));
	public static final ZetaRegistry REGISTRY = ZETA.createRegistry(MOD_ID); //TODO: push into Zeta probably

	public Quark() {
		instance = this;

		proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
		proxy.start();
	}

}
