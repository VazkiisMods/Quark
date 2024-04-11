package org.violetmoon.quark.mixin.mixins.accessor;

import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TreeConfiguration.class)
public interface AccessorTreeConfiguration {
    @Accessor("trunkProvider")
    @Mutable
    void quark$trunkProvider(BlockStateProvider trunkProvider);
}
