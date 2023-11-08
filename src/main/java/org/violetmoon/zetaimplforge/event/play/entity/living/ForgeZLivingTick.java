package org.violetmoon.zetaimplforge.event.play.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.violetmoon.zeta.event.play.entity.living.ZLivingTick;

public record ForgeZLivingTick(LivingEvent.LivingTickEvent e) implements ZLivingTick {
	@Override
	public LivingEntity getEntity() {
		return e.getEntity();
	}
}
