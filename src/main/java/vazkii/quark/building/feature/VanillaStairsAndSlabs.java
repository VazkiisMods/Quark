/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 19:34:51 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.block.BlockModSlab;
import vazkii.quark.base.block.BlockModStairs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.slab.BlockVanillaSlab;

public class VanillaStairsAndSlabs extends Feature {

	boolean stone, granite, diorite, andesite, endBricks, prismarine, prismarineBricks, darkPrismarine;

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
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		add("stone", Blocks.stone, 0, false, true, stone);
		add("stone_granite", Blocks.stone, 1, granite);
		add("stone_diorite", Blocks.stone, 3, diorite);
		add("stone_andesite", Blocks.stone, 5, andesite);
		add("end_bricks", Blocks.end_bricks, 0, endBricks);
		add("prismarine", Blocks.prismarine, 0, prismarine);
		add("prismarine_bricks", Blocks.prismarine, 1, prismarineBricks);
		add("prismarine_dark", Blocks.prismarine, 2, darkPrismarine);
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
			BlockModStairs.initStairs(block, meta, new BlockModStairs(stairsName, state));
		if(slab)
			BlockModSlab.initSlab(block, meta, new BlockVanillaSlab(slabName, state, false), new BlockVanillaSlab(slabName, state, true));
	}

}

