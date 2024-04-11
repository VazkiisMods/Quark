package org.violetmoon.quark.mixin.mixins.accessor;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(BlockEntityType.class)
public interface AccessorBlockEntityType {
    @Accessor("validBlocks")
    Set<Block> quark$validBlocks();

    @Accessor("validBlocks")
    void quark$validBlocks(Set<Block> set);
}
