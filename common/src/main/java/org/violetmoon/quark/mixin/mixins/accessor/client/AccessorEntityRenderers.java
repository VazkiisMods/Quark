package org.violetmoon.quark.mixin.mixins.accessor.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderers.class)
public interface AccessorEntityRenderers {
    @Invoker("register")
    static <T extends Entity> void quark$register(EntityType<? extends T> entityType, EntityRendererProvider<T> provider) {
        throw new AssertionError();
    }
}
