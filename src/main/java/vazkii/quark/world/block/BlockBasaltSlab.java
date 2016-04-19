/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 18:52:53 (GMT)]
 */
package vazkii.quark.world.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockModSlab;

public class BlockBasaltSlab extends BlockModSlab {

	public BlockBasaltSlab(boolean doubleSlab) {
		super("stone_basalt_slab", Material.rock, doubleSlab);
		setHardness(1.5F);
		setResistance(10.0F);
		setStepSound(SoundType.STONE);
		setCreativeTab(CreativeTabs.tabBlock);
	}

}
