package org.violetmoon.quark.base.config.type;

import org.violetmoon.quark.base.config.ConfigFlagManager;
import org.violetmoon.zeta.module.ZetaModule;

public interface IConfigType {

	default void onReload(ZetaModule module, ConfigFlagManager flagManager) { }
	
}
