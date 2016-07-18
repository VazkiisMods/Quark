/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [17/07/2016, 03:52:11 (GMT)]
 */
package vazkii.quark.world.entity.arrow;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import vazkii.quark.world.feature.ExtraArrows;

public class EntityArrowTorch extends EntityArrow {

	public EntityArrowTorch(World worldIn) {
		super(worldIn);
	}
	
	public EntityArrowTorch(World worldIn, EntityLivingBase shooter) {
		super(worldIn, shooter);
	}
	
	public EntityArrowTorch(World worldIn, IPosition pos) {
		super(worldIn, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	protected ItemStack getArrowStack() {
		return new ItemStack(ExtraArrows.arrow_torch);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();

		if(worldObj.isRemote)
			spawnPotionParticles(1);
	}

	public void spawnPotionParticles(int particleCount) {
		if(particleCount > 0)
			for(int i = 0; i < particleCount; i++)
				worldObj.spawnParticle(EnumParticleTypes.FLAME, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, 0.0D, 0.0D, 0.0D, new int[0]);
	}

	@Override
	protected void onHit(RayTraceResult raytraceResultIn) {
		super.onHit(raytraceResultIn);

		if(!worldObj.isRemote) {
			BlockPos pos = raytraceResultIn.getBlockPos();

			if(pos != null) {
				EnumFacing facing = raytraceResultIn.sideHit;
				BlockPos torchPos = pos.offset(facing);
				Block torchBlock = worldObj.getBlockState(torchPos).getBlock();

				if(Blocks.TORCH.canPlaceBlockAt(worldObj, torchPos) && (worldObj.isAirBlock(torchPos) || torchBlock.isReplaceable(worldObj, torchPos))) {
					IBlockState finalState = Blocks.TORCH.onBlockPlaced(worldObj, torchPos, facing, 0, 0, 0, 0, null);
					worldObj.setBlockState(torchPos, finalState);
					setDead();
				}
			}
		}
	}

}
