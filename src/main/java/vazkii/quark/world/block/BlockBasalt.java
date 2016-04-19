/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 15:05:41 (GMT)]
 */
package vazkii.quark.world.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockMetaVariants;

public class BlockBasalt extends BlockMetaVariants {

	public BlockBasalt() {
		super("basalt", Material.rock, Variants.class);
		setHardness(1.5F);
		setResistance(10.0F);
		setStepSound(SoundType.STONE);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	public enum Variants implements EnumBase {
		STONE_BASALT,
		STONE_BASALT_SMOOTH
	}

}
