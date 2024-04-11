package org.violetmoon.quark.mixin.mixins.accessor.client;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractClientPlayer.class)
public interface AccessorAbstractClientPlayer {
    @Accessor("playerInfo")
    PlayerInfo quark$playerInfo();
}
