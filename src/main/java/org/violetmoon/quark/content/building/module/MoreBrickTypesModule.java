package org.violetmoon.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.BooleanSupplier;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.handler.VariantHandler;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

@ZetaLoadModule(category = "building")
public class MoreBrickTypesModule extends ZetaModule {

	@Config(flag = "blue_nether_bricks", description = "This also comes with a utility recipe for Red Nether Bricks") 
	public boolean enableBlueNetherBricks = true;

	@Config(flag = "sandstone_bricks", description = "This also includes Red Sandstone Bricks and Soul Sandstone Bricks") 
	public boolean enableSandstoneBricks = true;

	@Config(flag = "cobblestone_bricks", description = "This also includes Mossy Cobblestone Bricks")
	private static boolean enableCobblestoneBricks = true;
	
	@Config(flag = "blackstone_bricks", description = "Requires Cobblestone Bricks to be enabled")
	private static boolean enableBlackstoneBricks = true;
	
	@Config(flag = "dirt_bricks", description = "Requires Cobblestone Bricks to be enabled")
	private static boolean enableDirtBricks = true;
	
	@Config(flag = "netherrack_bricks", description = "Requires Cobblestone Bricks to be enabled")
	private static boolean enableNetherrackBricks = true;
	
	@LoadEvent
	public final void register(ZRegister event) {
		add("blue_nether", Blocks.NETHER_BRICKS, () -> enableBlueNetherBricks);
		
		add("sandstone", Blocks.SANDSTONE, () -> enableSandstoneBricks);
		add("red_sandstone", Blocks.RED_SANDSTONE, () -> enableSandstoneBricks);
		add("soul_sandstone", Blocks.SANDSTONE, () -> enableSandstoneBricks && Quark.ZETA.modules.isEnabled(SoulSandstoneModule.class));
		
		add("cobblestone", Blocks.COBBLESTONE, () -> enableCobblestoneBricks);
		add("mossy_cobblestone", Blocks.MOSSY_COBBLESTONE, () -> enableCobblestoneBricks);
		
		add("blackstone", Blocks.BLACKSTONE, () -> enableBlackstoneBricks && enableCobblestoneBricks);
		add("dirt", Blocks.DIRT, () -> enableDirtBricks && enableCobblestoneBricks);
		add("netherrack", Blocks.NETHERRACK, () -> enableNetherrackBricks && enableCobblestoneBricks);
	}
	
	private void add(String name, Block parent, BooleanSupplier cond) {
		VariantHandler.addSlabStairsWall(new QuarkBlock(name + "_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, 
				Block.Properties.copy(parent)
				.requiresCorrectToolForDrops())
				.setCondition(cond));
	}
	
}

