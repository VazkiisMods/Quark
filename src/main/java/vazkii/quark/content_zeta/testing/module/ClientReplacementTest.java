package vazkii.quark.content_zeta.testing.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import vazkii.quark.base.Quark;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(name = "Client Replacement Test", category = "testing")
public class ClientReplacementTest extends ZetaModule {
	protected boolean foo = false;
	protected Object wow = this;

	@LoadEvent
	public void commonSetup(ZCommonSetup e) {
		e.enqueueWork(() -> {
			Quark.LOG.info("CRT COMMON SETUP! class {}", this.getClass().getName());
			Quark.LOG.info("CRT COMMON SETUP! foo   {}", foo);
			Quark.LOG.info("CRT COMMON SETUP! obj   {}", wow);
		});
	}

	@ZetaLoadModule(clientReplacementOf = ClientReplacementTest.class)
	public static class Client extends ClientReplacementTest {
		public Client() {
			foo = true;
			wow = Minecraft.getInstance();
			Quark.LOG.info(Screen.hasShiftDown());
		}

		public static Minecraft Ooooo_spooky_client_only = Minecraft.getInstance();
	}
}
