package org.violetmoon.quark.content.automation.base;

import net.minecraft.util.StringRepresentable;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * @author WireSegal
 *         Created at 10:12 AM on 8/26/19.
 */
public enum RandomizerPowerState implements StringRepresentable {
	OFF, LEFT, RIGHT;

	@NotNull
	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}
}
