/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 16:37:01 (GMT)]
 */
package vazkii.quark.building.block.slab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockModSlab;

public class BlockHardenedClayTilesSlab extends BlockModSlab {

	public BlockHardenedClayTilesSlab(boolean doubleSlab) {
		super("hardened_clay_tiles_slab", Material.rock, doubleSlab);
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
