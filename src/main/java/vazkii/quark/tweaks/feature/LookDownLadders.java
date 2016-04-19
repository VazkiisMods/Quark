/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [18/03/2016, 23:50:45 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class LookDownLadders extends Feature {

	@SubscribeEvent
	public void onPlayerTick(LivingUpdateEvent event) {
		if(event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			if(player.isOnLadder() && !player.isSneaking() && player.moveForward == 0 && player.rotationPitch > 70)
				player.moveEntity(0, -0.2, 0);
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
