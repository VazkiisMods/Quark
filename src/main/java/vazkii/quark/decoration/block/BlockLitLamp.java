/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 21:35:57 (GMT)]
 */
package vazkii.quark.decoration.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockMod;

public class BlockLitLamp extends BlockMod {

	public BlockLitLamp() {
		super("lit_lamp", Material.glass);
		setHardness(0.3F);
		setLightLevel(1F);
		setStepSound(SoundType.GLASS);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

}
