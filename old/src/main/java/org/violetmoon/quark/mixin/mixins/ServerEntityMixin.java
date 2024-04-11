package org.violetmoon.quark.mixin.mixins;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownTrident;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.violetmoon.quark.content.tools.module.ColorRunesModule;

import java.util.function.Consumer;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {

	@Final
	@Shadow
	private Entity entity;

	@Invoker
	protected abstract void invokeBroadcastAndSend(Packet<?> packet);

	@Inject(method = "sendDirtyEntityData", at = @At("HEAD"))
	private void updateTridentData(CallbackInfo ci) {
		if(entity instanceof ThrownTrident trident)
			ColorRunesModule.syncTrident(this::invokeBroadcastAndSend, trident, false);
	}

	@Inject(method = "sendPairingData", at = @At("HEAD"))
	private void pairTridentData(ServerPlayer serverPlayer, Consumer<Packet<?>> send, CallbackInfo ci) {
		if(entity instanceof ThrownTrident trident)
			ColorRunesModule.syncTrident(send, trident, true);
	}

}
