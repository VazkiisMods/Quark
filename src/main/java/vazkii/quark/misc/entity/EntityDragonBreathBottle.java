package vazkii.quark.misc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vazkii.quark.misc.feature.ThrowableDragonBreath;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EntityDragonBreathBottle extends EntityThrowable {

	public EntityDragonBreathBottle(World world) {
		super(world);
	}

	public EntityDragonBreathBottle(World world, EntityLivingBase entity) {
		super(world, entity);
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult result) {		
		if(!world.isRemote) {
			
			if(result.typeOfHit != Type.BLOCK) {
				
				this.motionY *= -0.1;
				this.motionY *= -0.1;
				this.motionY *= -0.1;
				
			}else {
				
				List<BlockPos> coordsList = getCoordsToPut(result.getBlockPos());
				for(BlockPos coords : coordsList) {
					world.playEvent(2001, coords, Block.getStateId(world.getBlockState(coords)));
					world.setBlockState(coords, Blocks.END_STONE.getDefaultState());
				}

				((WorldServer) world).spawnParticle(EnumParticleTypes.DRAGON_BREATH, posX, posY - 4, posZ, 500, 2, 2, 2, 0.03);
				setDead();
				
			}			
		}		
	}

	private List<BlockPos> getCoordsToPut(BlockPos pos) {
		List<BlockPos> possibleCoords = new ArrayList<>();
		int range = 4;
		int rangeY = 4;

		for (BlockPos bPos : BlockPos.getAllInBox(pos.add(-range, -rangeY, -range), pos.add(range, rangeY, range))) {
			IBlockState state = world.getBlockState(bPos);
			Block block = state.getBlock();
			if(block.isReplaceableOreGen(state, world, bPos, BlockStateMatcher.forBlock(Blocks.STONE)))
				possibleCoords.add(bPos);
		}

		Collections.shuffle(possibleCoords, rand);

		return possibleCoords.stream().limit(ThrowableDragonBreath.blocksPerBottle).collect(Collectors.toList());
	}

}
