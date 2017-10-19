/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [30/03/2016, 15:37:23 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.block.BlockQuarkWall;
import vazkii.quark.base.module.Feature;

public class VanillaWalls extends Feature {

	boolean stone, granite, graniteSmooth, diorite, dioriteSmooth, andesite, andesiteSmooth, sandstone, redSandstone, stoneBricks, bricks, quartz, prismarine, prismarineBricks, darkPrismarine, purpurBlock, endBricks;

	@Override
	public void setupConfig() {
		stone = loadPropBool("Stone", "", true);
		granite = loadPropBool("Granite", "", true);
		graniteSmooth = loadPropBool("Polished Granite", "", true);
		diorite = loadPropBool("Diorite", "", true);
		dioriteSmooth = loadPropBool("Polished Diorite", "", true);
		andesite = loadPropBool("Andesite", "", true);
		andesiteSmooth = loadPropBool("Polished Andesite", "", true);
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
		add("stone", Blocks.STONE, 0, stone);
		add("stone_granite", Blocks.STONE, 1, granite);
		add("stone_granite_smooth", Blocks.STONE, 2, graniteSmooth);
		add("stone_diorite", Blocks.STONE, 3, diorite);
		add("stone_diorite_smooth", Blocks.STONE, 4, dioriteSmooth);
		add("stone_andesite", Blocks.STONE, 5, andesite);
		add("stone_andesite_smooth", Blocks.STONE, 6, andesiteSmooth);
		add("sandstone", Blocks.SANDSTONE, 0, sandstone);
		add("red_sandstone", Blocks.RED_SANDSTONE, 0, redSandstone);
		add("stonebrick", Blocks.STONEBRICK, 0, stoneBricks);
		add("brick", Blocks.BRICK_BLOCK, 0, bricks);
		add("quartz", Blocks.QUARTZ_BLOCK, 0, quartz);
		add("prismarine_rough", Blocks.PRISMARINE, 0, prismarine);
		add("prismarine_bricks", Blocks.PRISMARINE, 1, prismarineBricks);
		add("dark_prismarine", Blocks.PRISMARINE, 2, darkPrismarine);
		add("purpur_block", Blocks.PURPUR_BLOCK, 0, purpurBlock);
		add("end_bricks", Blocks.END_BRICKS, 0, endBricks);
	}

	public static void add(String name, Block block, int meta, boolean doit) {
		add(name, block, meta, doit, (wallName, state) -> new BlockQuarkWall(wallName, state));
	}

	public static void add(String name, Block block, int meta, boolean doit, WallSupplier supplier) {
		if(!doit)
			return;

		IBlockState state = block.getStateFromMeta(meta);
		String wallName = name + "_wall";
		BlockQuarkWall.initWall(block, meta, supplier.supply(wallName, state));
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

	public static interface WallSupplier {
		public BlockQuarkWall supply(String wallName, IBlockState state);
	}
}
