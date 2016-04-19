/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [18/04/2016, 22:35:31 (GMT)]
 */
package vazkii.quark.building.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockMod;

public class BlockSnowBricks extends BlockMod {

	public BlockSnowBricks() {
		super("snow_bricks", Material.craftedSnow);
		setHardness(0.2F);
		setStepSound(SoundType.SNOW);
		setCreativeTab(CreativeTabs.tabBlock);
	}

}
