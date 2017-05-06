/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [05/06/2016, 23:09:58 (GMT)]
 */
package vazkii.quark.misc.feature;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.item.ItemSlimeBucket;

public class SlimeBucket extends Feature {

	public static Item slime_bucket;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		slime_bucket = new ItemSlimeBucket();
	}

	@SubscribeEvent
	public void entityInteract(PlayerInteractEvent.EntityInteract event) {
		if(event.getTarget() != null) {
			String name = EntityList.getEntityString(event.getTarget());
			if(name != null && name.equals("Slime") && ((EntitySlime) event.getTarget()).getSlimeSize() == 1 && ((EntityLiving) event.getTarget()).getHealth() > 0) {
				EntityPlayer player = event.getEntityPlayer();
				EnumHand hand = EnumHand.MAIN_HAND;
				ItemStack stack = player.getHeldItemMainhand();
				if(stack.isEmpty() || stack.getItem() != Items.BUCKET) {
					stack = player.getHeldItemOffhand();
					hand = EnumHand.OFF_HAND;
				}

				if(!stack.isEmpty() && stack.getItem() == Items.BUCKET) {
					ItemStack outStack = new ItemStack(slime_bucket);
					if(stack.getCount() == 1)
						player.setHeldItem(hand, outStack);
					else {
						stack.shrink(1);
						if(stack.getCount() == 0)
							player.setHeldItem(hand, outStack);
						else if(!player.inventory.addItemStackToInventory(outStack))
							player.dropItem(outStack, false);
					}

					player.swingArm(hand);
					event.getTarget().setDead();
				}
			}
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
