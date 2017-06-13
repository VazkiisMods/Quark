package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockDuskbound;
import vazkii.quark.building.block.BlockDuskboundLantern;
import vazkii.quark.building.block.slab.BlockDuskboundSlab;
import vazkii.quark.building.block.stairs.BlockDuskboundStairs;

public class DuskboundBlocks extends Feature {

	public static Block duskbound_block;
	public static Block duskbound_lantern;

	boolean enableStairsAndSlabs;
	boolean enableWalls;

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
		enableWalls = loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		duskbound_block = new BlockDuskbound();
		duskbound_lantern = new BlockDuskboundLantern();

		if(enableStairsAndSlabs) {
			BlockModStairs.initStairs(duskbound_block, 0, new BlockDuskboundStairs());
			BlockModSlab.initSlab(duskbound_block, 0, new BlockDuskboundSlab(false), new BlockDuskboundSlab(true));
		}
		
		VanillaWalls.add("duskbound_block", duskbound_block, 0, enableWalls);
		
		RecipeHandler.addOreDictRecipe(new ItemStack(duskbound_block, 16), 
				"PPP", "POP", "PPP",
				'P', new ItemStack(Blocks.PURPUR_BLOCK),
				'O', new ItemStack(Blocks.OBSIDIAN));
		RecipeHandler.addOreDictRecipe(new ItemStack(duskbound_lantern, 4), 
				"DDD", "DED", "DDD",
				'D', new ItemStack(duskbound_block),
				'E', new ItemStack(Items.ENDER_PEARL));
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
