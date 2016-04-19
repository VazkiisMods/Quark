/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 22:30:09 (GMT)]
 */
package vazkii.quark.decoration.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockMod;

public class BlockBlazeLantern extends BlockMod {

	public BlockBlazeLantern() {
		super("blaze_lantern", Material.glass);
		setHardness(0.3F);
		setStepSound(SoundType.GLASS);
		setLightLevel(1.0F);
		setCreativeTab(CreativeTabs.tabBlock);
	}

}
