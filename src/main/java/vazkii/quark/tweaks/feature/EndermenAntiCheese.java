/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/06/2016, 00:52:11 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class EndermenAntiCheese extends Feature {

	int minimumDifficulty = 2;
	boolean oldBehaviour;
	int delay;
	int lowerBound;
	boolean ignoreMobGriefing;
	
	@Override
	public void setupConfig() {
		minimumDifficulty = loadPropInt("Minimum Difficulty", "The minimum difficulty in which this effect should take place. (1: easy, 2: normal, 3: hard)", 3);
		oldBehaviour = loadPropBool("Use Old Behaviour", "Set this to true to use the old behaviour, where the endermen would teleport the player to them", false);
		delay = loadPropInt("Delay", "The delay (in ticks) between how often an enderman can break a block.", 10);
		lowerBound = loadPropInt("HP Lower Bound", "A value of health for which endermen will stop doing anti-cheese when below. Set to 0 to disable.", 3);
		ignoreMobGriefing = loadPropBool("Ignore mobGriefing Gamerule", "", true);
	}

	@SubscribeEvent
	public void onUpdate(LivingUpdateEvent event) {
		if(event.getEntityLiving() instanceof EntityEnderman && event.getEntityLiving().getEntityWorld().getDifficulty().getDifficultyId() >= minimumDifficulty) {
			EntityEnderman entity = (EntityEnderman) event.getEntityLiving();
			
			if(entity.getHealth() < lowerBound)
				return;

			BlockPos ourPos = entity.getPosition().up(2);
			IBlockState ourState = entity.getEntityWorld().getBlockState(ourPos);
			Block ourBlock = ourState.getBlock();
			if(ourBlock.getCollisionBoundingBox(ourState, entity.getEntityWorld(), ourPos) != null)
				return;

			EntityLivingBase target = entity.getAttackTarget();
			if(target != null && target instanceof EntityPlayer && target.onGround) {
				BlockPos pos = target.getPosition().up(2);
				if(pos.getDistance(ourPos.getX(), ourPos.getY(), ourPos.getZ()) > 5)
					return;

				if(oldBehaviour)
					teleportPlayer(entity, target, pos);
				else pickupDefense(entity, target, ourPos);
			}
		}
	}
	
	private void teleportPlayer(EntityEnderman entity, EntityLivingBase target, BlockPos pos) {
		IBlockState state = entity.getEntityWorld().getBlockState(pos);
		Block block = state.getBlock();

		if(block.getCollisionBoundingBox(state, entity.getEntityWorld(), pos) != null) {
			for(int i = 0; i < 16; i++)
				if(target.attemptTeleport(entity.posX + (Math.random() - 0.5) * 2, entity.posY + 0.5, entity.posZ + (Math.random() - 0.5) * 2))
					break;

			target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 30, 0));
			target.getEntityWorld().playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ENDERMEN_SCREAM, SoundCategory.HOSTILE, 1F, 1F);
			target.getEntityWorld().playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1F, 1F);
		}
	}
	
	private void pickupDefense(EntityEnderman entity, EntityLivingBase target, BlockPos pos) {
		if(entity.ticksExisted % delay != 0 && (ignoreMobGriefing || !entity.world.getGameRules().getBoolean("mobGriefing")))
			return;

		Vec3d look = entity.getLookVec();
		pos = pos.add((int) (look.x * 1.2), 0, (int) (look.z * 1.2));
		entity.swingArm(EnumHand.MAIN_HAND);
		
		IBlockState state = entity.world.getBlockState(pos);
		Block block = state.getBlock();
		boolean unbreakable = block.getBlockHardness(state, entity.world, pos) == -1 || !block.canEntityDestroy(state, entity.world, pos, entity);
		if(!unbreakable && block.getCollisionBoundingBox(state, entity.getEntityWorld(), pos) != null) {
			List<ItemStack> drops = block.getDrops(entity.world, pos, state, 0);
			entity.world.setBlockToAir(pos);
			entity.world.playEvent(2001, pos, Block.getStateId(state));
			
			if(!target.world.isRemote)
				for(ItemStack drop : drops)
					entity.world.spawnEntity(new EntityItem(entity.world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
