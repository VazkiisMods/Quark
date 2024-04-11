package org.violetmoon.quark.content.client.resources;

public enum AttributeDisplayType {
	/**
	 * Displays as a shift from the default value.
	 * Use for things which are additions to the main value, and the main value is human-readable.
	 *
	 * Examples:
	 * For an item that increases max health by 10, this will show "+10".
	 * For an item that decreases reach distance by 2, this will show "-2".
	 */
	DIFFERENCE,
	/**
	 * Displays as a percentage of a chance.
	 * Use for either percentage chances, or for things where the base value is 1 and representing as a chance makes
	 * sense.
	 *
	 * Examples:
	 * For an item that increases luck by 1, this will show "+100%".
	 * For an item that decreases knockback resistance by 0.5, this will show "-50%".
	 */
	PERCENTAGE,
	/**
	 * Displays as a multiplier applied to the default value, even if that's not strictly true of the calculation.
	 * Use for something where the main value is not human-readable.
	 *
	 * Examples:
	 * For an item that increases a player's gravity by 0.08 (the default value), this will show "2x".
	 * For an item that divides a player's speed by two, this will show "0.5x".
	 */
	MULTIPLIER,
	/**
	 * Displays as a flat number.
	 * Use for a value where an item's value is simple to understand.
	 *
	 * Examples:
	 * For an item that gives the player 5 points of armor, this will show "5".
	 * For an item that has a swing speed of 1.5, this will show "1.5".
	 */
	FLAT
}
