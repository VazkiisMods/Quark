/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [24/03/2016, 15:20:10 (GMT)]
 */
package vazkii.quark.building.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockMetaVariants;

public class BlockBark extends BlockMetaVariants {

	public BlockBark() {
		super("bark", Material.wood, Variants.class);
		setHardness(2.0F);
		setStepSound(SoundType.WOOD);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	public enum Variants implements EnumBase {
		BARK_OAK,
		BARK_SPRUCE,
		BARK_BIRCH,
		BARK_JUNGLE,
		BARK_ACACIA,
		BARK_DARK_OAK
	}

}
