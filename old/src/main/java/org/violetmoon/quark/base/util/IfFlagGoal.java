package org.violetmoon.quark.base.util;

import net.minecraft.world.entity.ai.goal.Goal;

import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.function.BooleanSupplier;

/**
 * @author WireSegal
 *         Created at 12:32 PM on 9/8/19.
 */
public class IfFlagGoal extends Goal {
	private final Goal parent;
	private final BooleanSupplier isEnabled;

	public IfFlagGoal(Goal parent, BooleanSupplier isEnabled) {
		super();
		this.parent = parent;
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean canUse() {
		return isEnabled.getAsBoolean() && parent.canUse();
	}

	@Override
	public boolean canContinueToUse() {
		return isEnabled.getAsBoolean() && parent.canContinueToUse();
	}

	@Override
	public boolean isInterruptable() {
		return parent.isInterruptable();
	}

	@Override
	public void start() {
		parent.start();
	}

	@Override
	public void stop() {
		parent.stop();
	}

	@Override
	public void tick() {
		parent.tick();
	}

	@NotNull
	@Override
	public EnumSet<Flag> getFlags() {
		return parent.getFlags();
	}
}
