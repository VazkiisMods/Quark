package org.violetmoon.zetaimplforge.event.play.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.violetmoon.zeta.event.play.entity.living.ZLivingDeath;

public class ForgeZLivingDeath implements ZLivingDeath {
	private final LivingDeathEvent e;

	public ForgeZLivingDeath(LivingDeathEvent e) {
		this.e = e;
	}

	@Override
	public Entity getEntity() {
		return e.getEntity();
	}

	@Override
	public DamageSource getSource() {
		return e.getSource();
	}

	public static class Lowest extends ForgeZLivingDeath implements ZLivingDeath.Lowest {
		public Lowest(LivingDeathEvent e) {
			super(e);
		}
	}
}
