/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [18/04/2016, 19:46:53 (GMT)]
 */
package vazkii.quark.automation.block;

import java.awt.Point;import org.omg.IOP.ENCODING_CDR_ENCAPS;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.quark.automation.tile.TileRainDetector;
import vazkii.quark.base.block.BlockModContainer;

public class BlockRainDetector extends BlockModContainer {
	
    protected static final AxisAlignedBB RAIN_DETECTOR_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D);
	
    private static final PropertyBool POWER = PropertyBool.create("power");
    private static final PropertyBool INVERTED = PropertyBool.create("inverted");

	public BlockRainDetector() {
		super("rain_detector", Material.rock);
        setCreativeTab(CreativeTabs.tabRedstone);
        setHardness(1.5F);
        setStepSound(SoundType.STONE);
        
        setDefaultState(blockState.getBaseState().withProperty(POWER, false).withProperty(INVERTED, false));
    }

	@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return RAIN_DETECTOR_AABB;
    }

	@Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return blockState.getValue(POWER) ? 15 : 0;
    }

    public void updatePower(World worldIn, BlockPos pos) {
        if (!worldIn.provider.getHasNoSky()) {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            boolean raining = worldIn.isRaining();
            float f = worldIn.getCelestialAngleRadians(1.0F);
            
            if(iblockstate.getValue(INVERTED))
                raining = !raining;

            if(iblockstate.getValue(POWER) != raining)
                worldIn.setBlockState(pos, iblockstate.withProperty(POWER, raining), 3);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(playerIn.isAllowEdit()) {
            if(worldIn.isRemote)
                return true;
            else {
                worldIn.setBlockState(pos, state.withProperty(INVERTED, !state.getValue(INVERTED)), 4);
                updatePower(worldIn, pos);

                return true;
            }
        } else return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileRainDetector();
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { POWER, INVERTED });
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
    	return (state.getValue(POWER) ? 0b1 : 0) + (state.getValue(INVERTED) ? 0b10 : 0);
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
    	return getDefaultState().withProperty(POWER, (meta & 0b1) != 0).withProperty(INVERTED, (meta & 0b10) != 0);
    }

}
