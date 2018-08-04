/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/07/2016, 20:02:58 (GMT)]
 */
package vazkii.quark.world.entity;

import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import vazkii.quark.world.feature.DepthMobs;

public class EntityAshen extends EntitySkeleton {

	public EntityAshen(World worldIn) {
		super(worldIn);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		super.setEquipmentBasedOnDifficulty(difficulty);

		ItemStack stack = new ItemStack(Items.TIPPED_ARROW);
		PotionUtils.appendEffects(stack, ImmutableSet.of(new PotionEffect(MobEffects.BLINDNESS, 50, 0)));
		setHeldItem(EnumHand.OFF_HAND, stack);
		
		inventoryHandsDropChances[0] = 0;
		inventoryHandsDropChances[1] = 0;
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere() && posY < DepthMobs.upperBound;
	}

}
