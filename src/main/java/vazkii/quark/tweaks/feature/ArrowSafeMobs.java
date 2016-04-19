/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 03:36:20 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class ArrowSafeMobs extends Feature {

	@SubscribeEvent
	public void onAttacked(LivingAttackEvent event) {
		if(event.getSource() != null) {
			Entity attacker = event.getSource().getEntity();
			if(attacker != null && attacker.getRidingEntity() == event.getEntity())
				event.setCanceled(true);
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
