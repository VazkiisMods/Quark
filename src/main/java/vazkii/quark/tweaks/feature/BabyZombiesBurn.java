/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [28/03/2016, 22:03:46 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.entity.item.EntityBoat;
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
		if(event.getEntity() instanceof EntityZombie) {
			EntityZombie zombie = (EntityZombie) event.getEntity();

			if(zombie.worldObj.isDaytime() && !zombie.worldObj.isRemote && zombie.isChild()) {
				float f = zombie.getBrightness(1.0F);
				BlockPos blockpos = zombie.getRidingEntity() instanceof EntityBoat ? new BlockPos(zombie.posX, Math.round(zombie.posY), zombie.posZ).up() : new BlockPos(zombie.posX, Math.round(zombie.posY), zombie.posZ);

				if(f > 0.5F && zombie.worldObj.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && zombie.worldObj.canSeeSky(blockpos)) {
					boolean flag = true;
					ItemStack itemstack = zombie.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

					if(itemstack != null) {
						if(itemstack.isItemStackDamageable()) {
							itemstack.setItemDamage(itemstack.getItemDamage() + zombie.worldObj.rand.nextInt(2));

							if(itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
								zombie.renderBrokenItemStack(itemstack);
								zombie.setItemStackToSlot(EntityEquipmentSlot.HEAD, (ItemStack)null);
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
