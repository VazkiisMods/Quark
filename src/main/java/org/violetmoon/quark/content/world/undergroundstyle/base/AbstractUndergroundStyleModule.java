package org.violetmoon.quark.content.world.undergroundstyle.base;

import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.world.WorldGenHandler;
import org.violetmoon.quark.base.world.WorldGenWeights;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.module.ZetaModule;

import net.minecraft.world.level.levelgen.GenerationStep.Decoration;

public abstract class AbstractUndergroundStyleModule<T extends UndergroundStyle> extends ZetaModule {

	@Config
	public UndergroundStyleConfig<T> generationSettings;

	@Override
	public void postConstruct() {
		generationSettings = getStyleConfig();
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		WorldGenHandler.addGenerator(this, new UndergroundStyleGenerator<>(generationSettings, getStyleName()), Decoration.UNDERGROUND_DECORATION, WorldGenWeights.UNDERGROUND_BIOMES);
	}
	
	protected abstract String getStyleName();

	protected abstract UndergroundStyleConfig<T> getStyleConfig();

}
