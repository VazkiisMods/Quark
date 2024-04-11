package org.violetmoon.quark.mixin.mixins.accessor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface AccessorLivingEntity {
	@Accessor("lastHurtByPlayer")
	Player quark$lastHurtByPlayer();

	@Accessor("lastHurtByPlayerTime")
	int quark$lastHurtByPlayerTime();

	@Accessor("attackStrengthTicker")
	int quark$attackStrengthTicker();

	@Accessor("attackStrengthTicker")
	void quark$attackStrengthTicker(int attackStrengthTicker);
}
