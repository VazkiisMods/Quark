package vazkii.quark.building.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockFramedGlass extends BlockMod implements IQuarkBlock {

	public BlockFramedGlass() {
		super("framed_glass", Material.GLASS);
		setHardness(3.0F);
		setResistance(10.0F);
		setSoundType(SoundType.GLASS);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);

		setHarvestLevel("pickaxe", 1);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
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
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
        Block block = iblockstate.getBlock();

        return block == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }
    
}
