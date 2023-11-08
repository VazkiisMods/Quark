package org.violetmoon.quark.base.config.type;

import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.config.ConfigFlagManager;
import org.violetmoon.zeta.module.ZetaModule;

public class ConditionalEntitySpawnConfig extends EntitySpawnConfig {

	@Config
	public boolean enabled = true;

	public final String flag;
	
	public ConditionalEntitySpawnConfig(String flag, int spawnWeight, int minGroupSize, int maxGroupSize, BiomeTagConfig biomes) {
		super(spawnWeight, minGroupSize, maxGroupSize, biomes);
		this.flag = flag;
	}
	
	@Override
	public void onReload(ZetaModule module, ConfigFlagManager flagManager) {
		if(module != null)
			flagManager.putFlag(module, flag, enabled);
	}
	
	@Override 
	public boolean isEnabled() {
		return enabled && super.isEnabled();
	}
	

}
