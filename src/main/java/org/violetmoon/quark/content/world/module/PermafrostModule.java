package org.violetmoon.quark.content.world.module;

import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.quark.base.config.type.CompoundBiomeConfig;
import org.violetmoon.quark.base.handler.VariantHandler;
import org.violetmoon.quark.content.world.undergroundstyle.PermafrostStyle;
import org.violetmoon.quark.content.world.undergroundstyle.base.AbstractUndergroundStyleModule;
import org.violetmoon.quark.content.world.undergroundstyle.base.UndergroundStyleConfig;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.util.Hint;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

@ZetaLoadModule(category = "world")
public class PermafrostModule extends AbstractUndergroundStyleModule<PermafrostStyle> {

	@Hint public static QuarkBlock permafrost;
	
	@LoadEvent
	public final void register(ZRegister event) {
		permafrost = new QuarkBlock("permafrost", this, CreativeModeTab.TAB_BUILDING_BLOCKS, 
				Block.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE)
				.requiresCorrectToolForDrops()
				.strength(1.5F, 10F)
				.sound(SoundType.STONE));
		
		VariantHandler.addSlabStairsWall(permafrost);
		VariantHandler.addSlabStairsWall(new QuarkBlock("permafrost_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(permafrost)));
		
		generationSettings.biomeObj.setBlock(permafrost.defaultBlockState());
	}
	
	@Override
	protected UndergroundStyleConfig<PermafrostStyle> getStyleConfig() {
		UndergroundStyleConfig<PermafrostStyle> config = new UndergroundStyleConfig<>(new PermafrostStyle(), 2, 100, 30, 10, 5, CompoundBiomeConfig.fromBiomeReslocs(false, "minecraft:frozen_peaks"));
		config.minYLevel = 105;
		config.maxYLevel = 140;
		return config;
	}
	
	@Override
	protected String getStyleName() {
		return "permafrost";
	}

}
