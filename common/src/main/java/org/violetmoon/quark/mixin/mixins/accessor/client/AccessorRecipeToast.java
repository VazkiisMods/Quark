package org.violetmoon.quark.mixin.mixins.accessor.client;

import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(RecipeToast.class)
public interface AccessorRecipeToast {
    @Accessor("recipes")
    List<Recipe<?>> quark$recipes();
}
