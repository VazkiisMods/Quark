package vazkii.quark.content.world.undergroundstyle.base;

import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.world.WorldGenHandler;
import vazkii.quark.base.world.WorldGenWeights;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.bus.LoadEvent;

public abstract class AbstractUndergroundStyleModule<T extends UndergroundStyle> extends QuarkModule {

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
