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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityLookHelper;
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
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class EndermenAntiCheese extends Feature {

	int minimumDifficulty = 2;
	boolean oldBehaviour;
	int delay;
	boolean ignoreMobGriefing;
	
	@Override
	public void setupConfig() {
		minimumDifficulty = loadPropInt("Minimum Difficulty", "The minimum difficulty in which this effect should take place. (1: easy, 2: normal, 3: hard)", 2);
		oldBehaviour = loadPropBool("Use Old Behaviour", "Set this to true to use the old behaviour, where the endermen would teleport the player to them", false);
		delay = loadPropInt("Delay", "The delay between how often an enderman can break a block.", 10);
		ignoreMobGriefing = loadPropBool("Ignore mobGriefing Gamerule", "", true);
	}

	@SubscribeEvent
	public void onUpdate(LivingUpdateEvent event) {
		if(event.getEntityLiving() instanceof EntityEnderman && event.getEntityLiving().getEntityWorld().getDifficulty().getDifficultyId() >= minimumDifficulty) {
			EntityEnderman entity = (EntityEnderman) event.getEntityLiving();

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
		pos = pos.add((int) (look.xCoord * 1.2), 0, (int) (look.zCoord * 1.2));
		entity.swingArm(EnumHand.MAIN_HAND);
		
		IBlockState state = entity.world.getBlockState(pos);
		boolean unbreakable = state.getBlock().getBlockHardness(state, entity.world, pos) == -1 || !state.getBlock().canEntityDestroy(state, entity.world, pos, entity);
		if(!unbreakable && state.getBlock().getCollisionBoundingBox(state, entity.getEntityWorld(), pos) != null && EntityEnderman.getCarriable(state.getBlock())) {
			IBlockState carried = entity.getHeldBlockState();
			if(carried != null) {
				Block outBlock = carried.getBlock();
				int meta = outBlock.getMetaFromState(carried);
				ItemStack outStack = new ItemStack(outBlock, 1, meta);
				EntityItem out = new EntityItem(entity.world, entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ, outStack);
				entity.world.spawnEntity(out);
			}
			
			entity.world.playEvent(2001, pos, Block.getStateId(state));
			entity.setHeldBlockState(state);
			entity.world.setBlockToAir(pos);
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
