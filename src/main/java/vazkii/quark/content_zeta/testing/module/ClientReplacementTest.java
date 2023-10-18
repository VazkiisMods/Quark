package vazkii.quark.content_zeta.testing.module;

import net.minecraft.client.Minecraft;
import vazkii.quark.base.Quark;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(category = "testing")
public class ClientReplacementTest extends ZetaModule {
	@LoadEvent
	public void commonSetup(ZCommonSetup e) {
		e.enqueueWork(() -> Quark.LOG.info("CRT COMMON SETUP! class {}", this.getClass().getName()));
	}

	@ZetaLoadModule(clientReplacement = true)
	public static class Client extends ClientReplacementTest {
		public static Minecraft Ooooo_spooky_client_only = Minecraft.getInstance();
	}
}
