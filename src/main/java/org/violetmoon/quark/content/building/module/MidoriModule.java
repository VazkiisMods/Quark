package org.violetmoon.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.quark.base.block.QuarkPillarBlock;
import org.violetmoon.quark.base.handler.VariantHandler;
import org.violetmoon.quark.base.item.QuarkItem;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZLoadComplete;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

@ZetaLoadModule(category = "building")
public class MidoriModule extends ZetaModule {

	private static Item moss_paste;
	
	@LoadEvent
	public final void register(ZRegister event) {
		moss_paste = new QuarkItem("moss_paste", this, new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS));
		
		Block.Properties props = Block.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
				.requiresCorrectToolForDrops()
				.strength(1.5F, 6.0F);
		
		VariantHandler.addSlabAndStairs(new QuarkBlock("midori_block", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props));
		new QuarkPillarBlock("midori_pillar", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props);
	}

	@LoadEvent
	public void loadComplete(ZLoadComplete event) {
		event.enqueueWork(() -> {
			ComposterBlock.COMPOSTABLES.put(moss_paste, 0.5F);
		});
	}
	
}
