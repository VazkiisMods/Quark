package vazkii.quark.content_zeta.testing.module;

import vazkii.quark.base.Quark;
import vazkii.quark.content.tweaks.module.DoubleDoorOpeningModule;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZLoadComplete;
import vazkii.zeta.event.ZPlayNoteBlock;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(name = "quark_test", category = "testing")
public class QuarkTestZetaModule extends ZetaModule {
	public QuarkTestZetaModule() {
		Quark.LOG.info("QuarkZetaTestModule get constructed NERD!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}

	@LoadEvent
	public void commonSetup(ZCommonSetup event) {
		Quark.LOG.info("qwer {}", event);
		event.enqueueWork(() -> {
			Quark.LOG.info("COMMON SETUP");
			Quark.LOG.info("uiop {}", event);
		});
	}

	@LoadEvent
	public void loadComplete(ZLoadComplete event) {
		Quark.LOG.info("asdf {}", event);
		event.enqueueWork(() -> {
			Quark.LOG.info("LOAD COMPLETE... enabled {}", enabled);
			Quark.LOG.info("jkl; {}", event);

			Quark.LOG.info("DoubleDoorOpeningModule LOADED? {}", Quark.ZETA.modules.getOptional(DoubleDoorOpeningModule.class).isPresent());
			Quark.LOG.info("ClientReplacementTest   LOADED? {}", Quark.ZETA.modules.getOptional(ClientReplacementTest.class).isPresent());
		});
	}

	@PlayEvent
	public void doot(ZPlayNoteBlock event) {
		Quark.LOG.info("doot {}", event.getInstrument());
	}
}
