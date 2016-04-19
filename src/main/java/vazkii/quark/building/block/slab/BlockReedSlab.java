/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 23:45:26 (GMT)]
 */
package vazkii.quark.building.block.slab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockModSlab;

public class BlockReedSlab extends BlockModSlab {

	public BlockReedSlab(boolean doubleSlab) {
		super("reed_block_slab", Material.wood, doubleSlab);
		setHardness(0.5F);
		setStepSound(SoundType.WOOD);
		setCreativeTab(CreativeTabs.tabBlock);
	}

}
