package org.violetmoon.quark.mixin.mixins.accessor.client;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(LevelRenderer.class)
public interface AccessorLevelRenderer {
    @Accessor("playingRecords")
    Map<BlockPos, SoundInstance> quark$playingRecords();
}
