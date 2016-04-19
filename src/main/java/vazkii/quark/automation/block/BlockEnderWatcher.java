/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [18/04/2016, 21:48:06 (GMT)]
 */
package vazkii.quark.automation.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.quark.automation.tile.TileEnderWatcher;
import vazkii.quark.base.block.BlockModContainer;

public class BlockEnderWatcher extends BlockModContainer {

	public static PropertyBool WATCHED = PropertyBool.create("watched");
	
	public BlockEnderWatcher() {
		super("ender_watcher", Material.iron);
		setHardness(3F);
		setResistance(10F);
		setStepSound(SoundType.METAL);
		setDefaultState(blockState.getBaseState().withProperty(WATCHED, false));
		setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { WATCHED });
	}
	
    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }
	
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return blockState.getValue(WATCHED) ? 15 : 0;
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(WATCHED) ? 1 : 0;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(WATCHED, meta != 0);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEnderWatcher();
	}

}
