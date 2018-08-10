/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [24/03/2016, 02:45:00 (GMT)]
 */
package vazkii.quark.decoration.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.quark.base.block.BlockQuarkPane;

public class BlockPaperWall extends BlockQuarkPane {

	public BlockPaperWall(String name) {
		super(name, Material.CLOTH);
		setHardness(1.5F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Override
	public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
		return !state.getValue(NORTH) && !state.getValue(SOUTH) && !state.getValue(EAST) && !state.getValue(WEST);
	}
	
	@Override
	public boolean canPaneConnectTo(IBlockAccess world, BlockPos pos, EnumFacing dir) {
		return super.canPaneConnectTo(world, pos, dir) || world.getBlockState(pos.offset(dir)).getBlock() instanceof BlockPaperWall;
	}

}
