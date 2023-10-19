package vazkii.quark.content.world.module;

import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.handler.WoodSetHandler;
import vazkii.quark.base.handler.WoodSetHandler.WoodSet;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "world", antiOverlap = { "caverns_and_chasms" })
public class AzaleaWoodModule extends QuarkModule {

	public static WoodSet woodSet;

	@LoadEvent
	public final void register(ZRegister event) {
		woodSet = WoodSetHandler.addWoodSet(this, "azalea", MaterialColor.COLOR_LIGHT_GREEN, MaterialColor.COLOR_BROWN, true);
		//ugly I know but config is fired before this now
		//TODO: not actually fired by the config lol
		enabledStatusChanged(true, this.enabled, this.enabled);
	}

	public void enabledStatusChanged(boolean firstLoad, boolean oldStatus, boolean newStatus) {
		ConfiguredFeature<TreeConfiguration, ?> configured = null;
		try {
			configured = TreeFeatures.AZALEA_TREE.value();
		} catch(IllegalStateException e) {
			e.printStackTrace();
		}

		if(configured != null) {
			TreeConfiguration config = configured.config();

			if(newStatus && woodSet != null)
				config.trunkProvider = BlockStateProvider.simple(woodSet.log);
			else if(!firstLoad)
				config.trunkProvider = BlockStateProvider.simple(Blocks.OAK_LOG);
		}
	}

}
