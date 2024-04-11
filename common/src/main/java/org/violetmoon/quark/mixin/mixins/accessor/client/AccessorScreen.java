package org.violetmoon.quark.mixin.mixins.accessor.client;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface AccessorScreen {
    @Accessor("renderables")
    List<Renderable> quark$renderables();
}
