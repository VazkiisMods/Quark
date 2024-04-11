package org.violetmoon.quark.content.mobs.ai;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;

import org.violetmoon.quark.base.handler.QuarkSounds;
import org.violetmoon.quark.content.mobs.entity.Shiba;
import org.violetmoon.quark.mixin.mixins.accessor.AccessorAbstractArrow;

import java.util.EnumSet;

public class FetchArrowGoal extends Goal {

	private final Shiba shiba;
	private int timeToRecalcPath;
	private final PathNavigation navigator;
	private int timeTilNextJump = 20;

	public FetchArrowGoal(Shiba shiba) {
		this.shiba = shiba;
		this.navigator = shiba.getNavigation();

		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public void tick() {
		AbstractArrow fetching = shiba.getFetching();
		if(fetching == null)
			return;

		this.shiba.getLookControl().setLookAt(fetching, 10.0F, shiba.getMaxHeadXRot());
		if(--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = 10;
			if(!shiba.isLeashed() && !shiba.isPassenger()) {
				this.navigator.moveTo(fetching, 1.1);
			}
		}

		double dist = shiba.distanceTo(fetching);
		if (dist < 3 && fetching.isAlive()) {
			// Fetch normal arrow
			if (fetching.pickup == Pickup.ALLOWED) {
				shiba.setMouthItem(((AccessorAbstractArrow) fetching).quark$getPickupItem());
				fetching.discard();
			}

			// Eat infinity arrows that are within 1 block of range
			// anything above 1 block looks weird as it just gets booped out of existence
			if (dist < 1) {
				if (fetching.pickup == Pickup.DISALLOWED || fetching.pickup == Pickup.CREATIVE_ONLY) {
					shiba.level().playSound(null, shiba.blockPosition(), QuarkSounds.ENTITY_SHIBA_EAT_ARROW, SoundSource.NEUTRAL);
					fetching.discard();
				}
			}
		}

		timeTilNextJump--;
		if(timeTilNextJump <= 0) {
			timeTilNextJump = shiba.level().random.nextInt(5) + 10;

			if(shiba.onGround()) {
				shiba.push(0, 0.3, 0);
				shiba.setJumping(true);
			}
		}
	}

	@Override
	public boolean canContinueToUse() {
		return canUse();
	}

	@Override
	public boolean canUse() {
		AbstractArrow fetching = shiba.getFetching();
		return shiba.getMouthItem().isEmpty() && fetching != null && fetching.isAlive() && fetching.level() == shiba.level() && fetching.pickup != Pickup.DISALLOWED;
	}

}
