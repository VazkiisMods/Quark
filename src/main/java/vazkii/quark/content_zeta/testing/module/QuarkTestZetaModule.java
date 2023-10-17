package vazkii.quark.content_zeta.testing.module;

import vazkii.quark.base.Quark;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(id = "quark_test")
public class QuarkTestZetaModule extends ZetaModule {
	public QuarkTestZetaModule() {
		for(int i = 0; i < 10; i++)	Quark.LOG.info("QuarkZetaTestModule get constructed NERD!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}
}
