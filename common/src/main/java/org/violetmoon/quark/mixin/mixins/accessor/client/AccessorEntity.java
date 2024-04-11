package org.violetmoon.quark.mixin.mixins.accessor.client;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface AccessorEntity {
    @Accessor("isInsidePortal")
    boolean quark$isInsidePortal();
}
