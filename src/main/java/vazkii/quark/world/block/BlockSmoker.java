package vazkii.quark.world.block;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockModContainer;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.world.tile.TileSmoker;

public class BlockSmoker extends BlockModContainer implements IQuarkBlock {

	public BlockSmoker() {
		super("smoker", Material.ROCK);
		setCreativeTab(CreativeTabs.DECORATIONS);
		setHardness(0.4F);
		setSoundType(SoundType.STONE);
	}
	
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
    	return new TileSmoker();
    }

}
