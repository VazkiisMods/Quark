package org.violetmoon.quark.mixin.mixins.accessor.client;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Deque;

@Mixin(ToastComponent.class)
public interface AccessorToastComponent {
    @Accessor("queued")
    Deque<Toast> quark$queued();
}
