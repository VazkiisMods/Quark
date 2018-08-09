package vazkii.quark.building.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.quark.base.block.BlockQuarkPane;
import vazkii.quark.decoration.block.BlockPaperWall;

public class BlockFramedGlassPane extends BlockQuarkPane {

	public BlockFramedGlassPane() {
		super("framed_glass_pane", Material.GLASS);
		setHardness(3.0F);
		setResistance(10.0F);
		setSoundType(SoundType.GLASS);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		
		setHarvestLevel("pickaxe", 1);
	}
	
	@Override
	public boolean canPaneConnectTo(IBlockAccess world, BlockPos pos, EnumFacing dir) {
		return super.canPaneConnectTo(world, pos, dir) || world.getBlockState(pos.offset(dir)).getBlock() instanceof BlockFramedGlass;
	}

	
}
