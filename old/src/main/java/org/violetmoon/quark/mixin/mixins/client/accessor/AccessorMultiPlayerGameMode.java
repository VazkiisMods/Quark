package org.violetmoon.quark.mixin.mixins.client.accessor;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultiPlayerGameMode.class)
public interface AccessorMultiPlayerGameMode {
	@Invoker("performUseItemOn")
	InteractionResult quark$performUseItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult hit);
}
