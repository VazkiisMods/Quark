package vazkii.quark.misc.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.BlockQuarkDust;
import vazkii.quark.misc.feature.PlaceVanillaDusts;

public class BlockGunpowder extends BlockQuarkDust {

	public static final PropertyBool LIT = PropertyBool.create("lit");
	
	public BlockGunpowder() {
		super("gunpowder_block");
		
		setDefaultState(getDefaultState().withProperty(LIT, false));
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!state.getValue(LIT)) {
			ItemStack stack = playerIn.getHeldItem(hand);
			boolean allow = false;
			SoundEvent sound = null;
			
			if(stack.getItem() == Items.FLINT_AND_STEEL) {
				stack.damageItem(1, playerIn);
				sound = SoundEvents.ITEM_FLINTANDSTEEL_USE;
				allow = true;
			} else if(stack.getItem() == Items.FIRE_CHARGE) {
				stack.shrink(1);
				sound = SoundEvents.ITEM_FIRECHARGE_USE;
				allow = true;
			}
			
			if(allow) {
				worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), sound, SoundCategory.BLOCKS, 1F, worldIn.rand.nextFloat() * 0.4F + 0.8F);
				lightUp(worldIn, pos);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!state.getValue(LIT)) {
			IBlockState otherBlock = worldIn.getBlockState(fromPos);
			if(otherBlock.getBlock() == Blocks.FIRE)
				lightUp(worldIn, pos);
		}
		
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
	}
	
	private boolean lightUp(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == this) {
			IBlockState belowState = world.getBlockState(pos.down());
			IBlockState newState = state.withProperty(LIT, true);
			world.setBlockState(pos, newState);
			world.scheduleUpdate(pos, newState.getBlock(),
					belowState.getBlock().getRegistryName().getResourcePath().contains("netherrack")
						? PlaceVanillaDusts.gunpowderDelayNetherrack 
						: PlaceVanillaDusts.gunpowderDelay);
			
			if(world instanceof WorldServer) {
	    		float x = pos.getX();
	        	float y = pos.getY() + 0.2F;
	        	float z = pos.getZ();
	        	
	        	((WorldServer) world).spawnParticle(EnumParticleTypes.FLAME, x + 0.5, y, z + 0.5, 6, 0.2, 0.0, 0.2, 0);
	        	((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + 0.5, y, z + 0.5, 6, 0.2, 0.0, 0.2, 0);
			}
			
			return true;
		} else if(state.getBlock() == Blocks.TNT) {
            state.getBlock().onBlockDestroyedByPlayer(world, pos, state.withProperty(BlockTNT.EXPLODE, Boolean.valueOf(true)));
            world.setBlockToAir(pos);
            
            return true;
		}
		
		return false;
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		worldIn.setBlockToAir(pos);
		
		if(worldIn instanceof WorldServer)
			((WorldServer) worldIn).spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 8, 0.2, 0.0, 0.2, 0);
		
		for(EnumFacing face : EnumFacing.HORIZONTALS) {
			EnumAttachPosition attach = getAttachPosition(worldIn, pos, face);
			BlockPos off = pos.offset(face);
			switch(attach) {
			case UP:
				lightUp(worldIn, off.up());
				break;
			case SIDE:
				if(!lightUp(worldIn, off))
					lightUp(worldIn, off.down());
				break;
			default: break;
			}
		}
	}
	
	@Override
	protected boolean canConnectTo(IBlockState blockState, EnumFacing side, IBlockAccess world, BlockPos pos) {
		Block block = blockState.getBlock();
		return block == this || block == Blocks.TNT;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)  {
		return getDefaultState().withProperty(LIT, meta != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(LIT) ? 1 : 0;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { NORTH, EAST, SOUTH, WEST, LIT });
	}
	
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    	if(stateIn.getValue(LIT)) {
    		float x = pos.getX() + 0.2F + rand.nextFloat() * 0.6F;
        	float y = pos.getY() + 0.2F;
        	float z = pos.getZ() + 0.2F + rand.nextFloat() * 0.6F;
        	
        	worldIn.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0, 0);
        	worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 0, 0, 0);
    	}
    }
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.GUNPOWDER;
	}

	@Override
	public int getColor(IBlockAccess world, IBlockState state, BlockPos pos, int tint) {
		return state.getValue(LIT) ? 0xFF6C00 : 0x555555;
	}

}
