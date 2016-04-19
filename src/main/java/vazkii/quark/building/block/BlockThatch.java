/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 22:41:27 (GMT)]
 */
package vazkii.quark.building.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockMod;

public class BlockThatch extends BlockMod {

	public BlockThatch() {
		super("thatch", Material.plants);
		setHardness(0.5F);
		setStepSound(SoundType.PLANT);
		setCreativeTab(CreativeTabs.tabBlock);
	}

}
