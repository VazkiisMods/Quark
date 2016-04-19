/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [25/03/2016, 21:13:18 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.handler.ItemNBTHelper;
import vazkii.quark.base.module.Feature;

public class SnowGolemPlayerHeads extends Feature {

	@SubscribeEvent
	public void onDrops(LivingDropsEvent event) {
		Entity e = event.getEntity();

		if(e.hasCustomName() && e instanceof EntitySnowman && event.getSource().getEntity() != null && event.getSource().getEntity() instanceof EntityWitch) {
			EntitySnowman snowman = (EntitySnowman) e;
			if(!snowman.isPumpkinEquipped()) { // for some reason isPumpkinEquipped is backwards
				ItemStack stack = new ItemStack(Items.skull, 1, 3);
				ItemNBTHelper.setString(stack, "SkullOwner", e.getCustomNameTag());
				event.getDrops().add(new EntityItem(e.worldObj, e.posX, e.posY, e.posZ, stack));
			}
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
