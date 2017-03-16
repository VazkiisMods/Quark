package vazkii.quark.world.block;

import java.util.Random;

import net.minecraft.block.BlockDirt;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockGlowcelium extends BlockMod implements IQuarkBlock {

	public BlockGlowcelium() {
		super("glowcelium", Material.GRASS);
		setTickRandomly(true);
		setHardness(0.2F);
		setLightLevel(0.5F);
		setSoundType(SoundType.PLANT);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(!worldIn.isRemote) {
			if(worldIn.getBlockState(pos.up()).getLightOpacity(worldIn, pos.up()) > 2)
				worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
			else for(int i = 0; i < 4; ++i) {
					BlockPos blockpos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
					IBlockState iblockstate = worldIn.getBlockState(blockpos);
					IBlockState iblockstate1 = worldIn.getBlockState(blockpos.up());

					if(iblockstate.getBlock() == Blocks.DIRT && iblockstate.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT && iblockstate1.getLightOpacity(worldIn, blockpos.up()) <= 2)
						worldIn.setBlockState(blockpos, getDefaultState());
				}
		}
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		super.randomDisplayTick(stateIn, worldIn, pos, rand);

		if(rand.nextInt(40) == 0)
			worldIn.spawnParticle(EnumParticleTypes.END_ROD, pos.getX() + rand.nextFloat(), pos.getY() + 1.15F, pos.getZ() + rand.nextFloat(), 0, 0, 0, new int[0]);
	}
	
	@Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Blocks.DIRT.getItemDropped(Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), rand, fortune);
    }

}