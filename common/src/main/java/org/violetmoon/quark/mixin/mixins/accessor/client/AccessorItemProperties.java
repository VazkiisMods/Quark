package org.violetmoon.quark.mixin.mixins.accessor.client;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemProperties.class)
public interface AccessorItemProperties {
    @Invoker("register")
    static void quark$register(Item item, ResourceLocation name, ClampedItemPropertyFunction property) {
        throw new AssertionError();
    }
}
