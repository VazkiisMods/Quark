/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 14, 2019, 19:51 AM (EST)]
 */
package org.violetmoon.quark.content.mobs.ai;

import net.minecraft.world.entity.ai.goal.Goal;

import org.violetmoon.quark.content.mobs.entity.Crab;

import java.util.EnumSet;

public class RaveGoal extends Goal {
	private final Crab crab;

	public RaveGoal(Crab crab) {
		this.crab = crab;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
	}

	@Override
	public boolean canUse() {
		return crab.isRaving();
	}

	@Override
	public void start() {
		this.crab.getNavigation().stop();
	}
}
