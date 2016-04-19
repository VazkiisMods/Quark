/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [18/04/2016, 22:56:01 (GMT)]
 */
package vazkii.quark.building.block.slab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockModSlab;

public class BlockSnowBricksSlab extends BlockModSlab {

	public BlockSnowBricksSlab(boolean doubleSlab) {
		super("snow_bricks_slab", Material.craftedSnow, doubleSlab);
		setHardness(0.2F);
		setStepSound(SoundType.SNOW);
		setCreativeTab(CreativeTabs.tabBlock);
	}

}
