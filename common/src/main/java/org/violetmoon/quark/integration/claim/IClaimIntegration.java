package org.violetmoon.quark.integration.claim;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.NotNull;

public interface IClaimIntegration {

	default boolean canBreak(@NotNull Player player, @NotNull BlockPos pos) {
		return true;
	}

	default boolean canPlace(@NotNull Player player, @NotNull BlockPos pos) {
		return true;
	}

	default boolean canReplace(@NotNull Player player, @NotNull BlockPos pos) {
		return true;
	}

	default boolean canAttack(@NotNull Player player, @NotNull Entity victim) {
		return true;
	}

	default boolean canInteract(@NotNull Player player, @NotNull BlockPos targetPos) {
		return true;
	}

	class Dummy implements IClaimIntegration {
	}

}
