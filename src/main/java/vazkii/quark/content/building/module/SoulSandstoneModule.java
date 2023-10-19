package vazkii.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "building")
public class SoulSandstoneModule extends QuarkModule {

	@LoadEvent
	public final void register(ZRegister event) {
		Block.Properties props = Block.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
				.requiresCorrectToolForDrops()
				.strength(0.8F);
		
		VariantHandler.addSlabStairsWall(new QuarkBlock("soul_sandstone", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props));
		new QuarkBlock("chiseled_soul_sandstone", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props);
		VariantHandler.addSlab(new QuarkBlock("cut_soul_sandstone", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props));
		VariantHandler.addSlabAndStairs(new QuarkBlock("smooth_soul_sandstone", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props));
	}
	
}
