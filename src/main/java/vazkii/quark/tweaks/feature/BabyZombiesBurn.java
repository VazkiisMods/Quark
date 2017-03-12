/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [28/03/2016, 22:03:46 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class BabyZombiesBurn extends Feature {

	@SubscribeEvent
	public void entityUpdate(LivingUpdateEvent event) {
		if(event.getEntity() instanceof EntityZombie && !(event.getEntity() instanceof EntityHusk)) {
			EntityZombie zombie = (EntityZombie) event.getEntity();

			if(zombie.getEntityWorld().isDaytime() && !zombie.getEntityWorld().isRemote && zombie.isChild()) {
				float f = zombie.getBrightness(1.0F);
				BlockPos blockpos = zombie.getRidingEntity() instanceof EntityBoat ? new BlockPos(zombie.posX, Math.round(zombie.posY), zombie.posZ).up() : new BlockPos(zombie.posX, Math.round(zombie.posY), zombie.posZ);

				if(f > 0.5F && zombie.getEntityWorld().rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && zombie.getEntityWorld().canSeeSky(blockpos)) {
					boolean flag = true;
					ItemStack itemstack = zombie.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

					if(!itemstack.isEmpty()) {
						if(itemstack.isItemStackDamageable()) {
							itemstack.setItemDamage(itemstack.getItemDamage() + zombie.getEntityWorld().rand.nextInt(2));

							if(itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
								zombie.renderBrokenItemStack(itemstack);
								zombie.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
							}
						}

						flag = false;
					}

					if(flag)
						zombie.setFire(8);
				}
			}
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
