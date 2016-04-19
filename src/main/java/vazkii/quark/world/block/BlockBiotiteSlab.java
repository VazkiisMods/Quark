/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [18/04/2016, 18:18:35 (GMT)]
 */
package vazkii.quark.world.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockModSlab;

public class BlockBiotiteSlab extends BlockModSlab {

	public BlockBiotiteSlab(boolean doubleSlab) {
		super("biotite_slab", Material.rock, doubleSlab);
		setStepSound(SoundType.STONE);
		setHardness(0.8F);	
		setCreativeTab(CreativeTabs.tabBlock);
	}

}
