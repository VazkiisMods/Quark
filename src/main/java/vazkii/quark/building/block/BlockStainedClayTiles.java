/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [19/03/2016, 01:43:43 (GMT)]
 */
package vazkii.quark.building.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockMetaVariants;

public class BlockStainedClayTiles extends BlockMetaVariants {

	public BlockStainedClayTiles() {
		super("stained_clay_tiles", Material.rock, Variants.class);
		setHardness(1.25F);
		setResistance(7.0F);
		setStepSound(SoundType.STONE);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	public static enum Variants implements EnumBase {
		STAINED_CLAY_TILES_WHITE,
		STAINED_CLAY_TILES_ORANGE,
		STAINED_CLAY_TILES_MAGENTA,
		STAINED_CLAY_TILES_LIGHT_BLUE,
		STAINED_CLAY_TILES_YELLOW,
		STAINED_CLAY_TILES_LIME,
		STAINED_CLAY_TILES_PINK,
		STAINED_CLAY_TILES_GRAY,
		STAINED_CLAY_TILES_SILVER,
		STAINED_CLAY_TILES_CYAN,
		STAINED_CLAY_TILES_PURPLE,
		STAINED_CLAY_TILES_BLUE,
		STAINED_CLAY_TILES_BROWN,
		STAINED_CLAY_TILES_GREEN,
		STAINED_CLAY_TILES_RED,
		STAINED_CLAY_TILES_BLACK
	}

}
