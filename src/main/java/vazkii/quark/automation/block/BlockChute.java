package vazkii.quark.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockModContainer;
import vazkii.quark.automation.tile.TileChute;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.decoration.feature.VariedChests;

public class BlockChute extends BlockModContainer implements IQuarkBlock {

    public static final PropertyBool ENABLED = PropertyBool.create("enabled");
	
	public BlockChute() {
		super("chute", Material.WOOD);
		setHardness(2.5F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(CreativeTabs.REDSTONE);
		
		setDefaultState(getDefaultState().withProperty(ENABLED, true));
	}
	
	@Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        boolean flag = !worldIn.isBlockPowered(pos);

        if(flag != state.getValue(ENABLED))
            worldIn.setBlockState(pos, state.withProperty(ENABLED, Boolean.valueOf(flag)), 2 | 4);
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileChute();
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
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { ENABLED });
	}
	
	@Override
	public IProperty[] getIgnoredProperties() {
		return new IProperty[] { ENABLED };
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(ENABLED) ? 0b0 : 1);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ENABLED, (meta & 0b1) != 1);
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.UP ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
	
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }
    
    @Override
    public boolean isTopSolid(IBlockState state) {
        return true;
    }

}
