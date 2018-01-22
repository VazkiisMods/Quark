package vazkii.quark.misc.block;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.BlockQuarkDust;

public class BlockGlowstoneDust extends BlockQuarkDust {

	public BlockGlowstoneDust() {
		super("glowstone_dust_block");
		setLightLevel(14F / 16F);
		disableStats();
	}

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    	if(rand.nextFloat() < 0.1) {
    		float x = pos.getX() + 0.3F + rand.nextFloat() * 0.4F;
        	float y = pos.getY() + 0.2F;
        	float z = pos.getZ() + 0.3F + rand.nextFloat() * 0.4F;
        	
        	worldIn.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 1.0, 1.0, 0);
    	}
    }
    
    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) { 
    	// NO-OP
    }
    
	@Override
	public int getColor(IBlockAccess world, IBlockState state, BlockPos pos, int tint) {
		return 0xfff000;
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.GLOWSTONE_DUST;
	}
}
