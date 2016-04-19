/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [27/03/2016, 21:55:50 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class ArmedArmorStands extends Feature {

	@SubscribeEvent
	public void entityConstruct(EntityEvent.EntityConstructing event) {
		if(event.getEntity() instanceof EntityArmorStand) {
			EntityArmorStand stand = (EntityArmorStand) event.getEntity();
			if(!stand.getShowArms())
				setShowArms(stand, true);
		}
	}

	private void setShowArms(EntityArmorStand e, boolean showArms) {
		e.getDataManager().set(EntityArmorStand.STATUS, Byte.valueOf(func_184797_a(e.getDataManager().get(EntityArmorStand.STATUS).byteValue(), 4, showArms)));
	}

	// idk, copypasta from EntityArmorStand
	private byte func_184797_a(byte p_184797_1_, int p_184797_2_, boolean p_184797_3_) {
		if (p_184797_3_)
			p_184797_1_ = (byte)(p_184797_1_ | p_184797_2_);
		else
			p_184797_1_ = (byte)(p_184797_1_ & ~p_184797_2_);

		return p_184797_1_;
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
