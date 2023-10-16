package vazkii.quark.content.building.module;

import java.util.function.Consumer;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.block.QuarkPillarBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.BUILDING)
public class MidoriModule extends QuarkModule {

	private static Item moss_paste;
	
	@Override
	public void register() {
		moss_paste = new QuarkItem("moss_paste", this, new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS));
		
		Block.Properties props = Block.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
				.requiresCorrectToolForDrops()
				.strength(1.5F, 6.0F);
		
		VariantHandler.addSlabAndStairs(new QuarkBlock("midori_block", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props));
		new QuarkPillarBlock("midori_pillar", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props);
	}
	
	@Override
	public void loadComplete(Consumer<Runnable> enqueueWork) {
		enqueueWork.accept(() -> {
			ComposterBlock.COMPOSTABLES.put(moss_paste, 0.5F);
		});
	}
	
}
