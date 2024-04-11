package org.violetmoon.quark.mixin.mixins.accessor;

import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerMenu.class)
public interface AccessorAbstractContainerMenu {
    @Accessor("containerId")
    @Mutable
    void quark$qontainerId(int containerId);
}
