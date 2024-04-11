package org.violetmoon.quark.mixin.mixins.accessor;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface AccessorSlot {
    @Accessor("y")
    int quark$y();

    @Accessor("y")
    @Mutable
    void quark$y(int y);
}
