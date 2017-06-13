/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 19:34:51 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.slab.BlockVanillaSlab;

public class VanillaStairsAndSlabs extends Feature {

	boolean stone, granite, diorite, andesite, endBricks, prismarine, prismarineBricks, darkPrismarine, redNetherBricks;

	@Override
	public void setupConfig() {
		stone = loadPropBool("Stone", "", true);
		granite = loadPropBool("Granite", "", true);
		diorite = loadPropBool("Diorite", "", true);
		andesite = loadPropBool("Andesite", "", true);
		endBricks = loadPropBool("End Bricks", "", true);
		prismarine = loadPropBool("Prismarine", "", true);
		prismarineBricks = loadPropBool("Prismarine Bricks", "", true);
		darkPrismarine = loadPropBool("Dark Prismarine", "", true);
		redNetherBricks = loadPropBool("Red Nether Brick", "", true);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		if(!GlobalConfig.enableVariants)
			return;
		
		add("stone", Blocks.STONE, 0, false, true, stone);
		add("stone_granite", Blocks.STONE, 1, granite);
		add("stone_diorite", Blocks.STONE, 3, diorite);
		add("stone_andesite", Blocks.STONE, 5, andesite);
		add("end_bricks", Blocks.END_BRICKS, 0, endBricks);
		add("prismarine", Blocks.PRISMARINE, 0, prismarine);
		add("prismarine_bricks", Blocks.PRISMARINE, 1, prismarineBricks);
		add("prismarine_dark", Blocks.PRISMARINE, 2, darkPrismarine);
		add("red_nether_brick", Blocks.RED_NETHER_BRICK, 0, redNetherBricks);
	}

	public void add(String name, Block block, int meta, boolean doit) {
		add(name, block, meta, true, true, doit);
	}

	public void add(String name, Block block, int meta, boolean slab, boolean stairs, boolean doit) {
		if(!doit)
			return;

		IBlockState state = block.getStateFromMeta(meta);
		String stairsName = name + "_stairs";
		String slabName = name + "_slab";

		if(stairs)
			BlockModStairs.initStairs(block, meta, new BlockQuarkStairs(stairsName, state));
		if(slab)
			BlockModSlab.initSlab(block, meta, new BlockVanillaSlab(slabName, state, false), new BlockVanillaSlab(slabName, state, true));
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}

