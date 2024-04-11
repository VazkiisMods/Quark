package org.violetmoon.quark.mixin.mixins.accessor.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockEntityRenderers.class)
public interface AccessorBlockEntityRenderers {
    @Invoker("register")
    static <T extends BlockEntity> void quark$register(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T> renderProvider) {
        throw new AssertionError();
    }
}
