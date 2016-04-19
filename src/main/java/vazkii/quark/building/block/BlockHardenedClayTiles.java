/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [19/03/2016, 01:27:27 (GMT)]
 */
package vazkii.quark.building.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockMod;

public class BlockHardenedClayTiles extends BlockMod {

	public BlockHardenedClayTiles() {
		super("hardened_clay_tiles", Material.rock);
		setHardness(1.25F);
		setResistance(7.0F);
		setStepSound(SoundType.STONE);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public MapColor getMapColor(IBlockState state) {
		return MapColor.adobeColor;
	}

}
