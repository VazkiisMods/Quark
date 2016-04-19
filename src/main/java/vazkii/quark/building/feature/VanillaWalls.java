/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [30/03/2016, 15:37:23 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.block.BlockModWall;
import vazkii.quark.base.module.Feature;

public class VanillaWalls extends Feature {

	boolean stone, granite, diorite, andesite, sandstone, redSandstone, stoneBricks, bricks, quartz, prismarine, prismarineBricks, darkPrismarine, purpurBlock, endBricks;

	@Override
	public void setupConfig() {
		stone = loadPropBool("Stone", "", true);
		granite = loadPropBool("Granite", "", true);
		diorite = loadPropBool("Diorite", "", true);
		andesite = loadPropBool("Andesite", "", true);
		sandstone = loadPropBool("Sandstone", "", true);
		redSandstone = loadPropBool("Red Sandstone", "", true);
		stoneBricks = loadPropBool("Stone Bricks", "", true);
		bricks = loadPropBool("Bricks", "", true);
		quartz = loadPropBool("Quartz", "", true);
		prismarine = loadPropBool("Prismarine", "", true);
		prismarineBricks = loadPropBool("Prismarine Bricks", "", true);
		darkPrismarine = loadPropBool("Dark Prismarine", "", true);
		purpurBlock = loadPropBool("Purpur", "", true);
		endBricks = loadPropBool("End Bricks", "", true);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		add("stone", Blocks.stone, 0, stone);
		add("stone_granite", Blocks.stone, 1, granite);
		add("stone_diorite", Blocks.stone, 3, diorite);
		add("stone_andesite", Blocks.stone, 5, andesite);
		add("sandstone", Blocks.sandstone, 0, sandstone);
		add("red_sandstone", Blocks.red_sandstone, 0, redSandstone);
		add("stonebrick", Blocks.stonebrick, 0, stoneBricks);
		add("brick", Blocks.brick_block, 0, bricks);
		add("quartz", Blocks.quartz_block, 0, quartz);
		add("prismarine_rough", Blocks.prismarine, 0, prismarine);
		add("prismarine_bricks", Blocks.prismarine, 1, prismarineBricks);
		add("dark_prismarine", Blocks.prismarine, 2, darkPrismarine);
		add("purpur_block", Blocks.purpur_block, 0, purpurBlock);
		add("end_bricks", Blocks.end_bricks, 0, endBricks);
	}

	public static void add(String name, Block block, int meta, boolean doit) {
		if(!doit)
			return;

		IBlockState state = block.getStateFromMeta(meta);
		String wallName = name + "_wall";
		BlockModWall.initWall(block, meta, new BlockModWall(wallName, state));
	}

}
