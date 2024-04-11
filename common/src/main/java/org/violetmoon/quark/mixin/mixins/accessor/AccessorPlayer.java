package org.violetmoon.quark.mixin.mixins.accessor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Player.class)
public interface AccessorPlayer {
    @Accessor("inventoryMenu")
    @Mutable
    void quark$inventoryMenu(InventoryMenu inventoryMenu);
}
