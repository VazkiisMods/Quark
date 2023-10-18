package vazkii.quark.content_zeta.testing.module;

import vazkii.quark.base.Quark;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(category = "testing")
public class SubclassModulesTest extends ZetaModule {
	public SubclassModulesTest() {
		Quark.LOG.info("SubclassModulesTest constructor");
	}

	protected boolean foo = false;

	@LoadEvent
	public void commonSetup(ZCommonSetup e) {
		Quark.LOG.info("subclass test COMMON SETUP! {} {}", this.getClass().getName(), foo);
	}

	@ZetaLoadModule(category = "testing")
	public static class StaticInnerClass extends SubclassModulesTest {
		public StaticInnerClass() {
			Quark.LOG.info("StaticInnerClass constructor");
			foo = true;
		}
	}
}
