package vazkii.quark.content.world.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.world.undergroundstyle.PermafrostStyle;
import vazkii.quark.content.world.undergroundstyle.base.AbstractUndergroundStyleModule;
import vazkii.quark.content.world.undergroundstyle.base.UndergroundStyleConfig;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "world")
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
