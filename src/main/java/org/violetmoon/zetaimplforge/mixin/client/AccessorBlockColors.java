package org.violetmoon.zetaimplforge.mixin.client;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.Holder.Reference;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(BlockColors.class)
public interface AccessorBlockColors {

	@Accessor("blockColors")
	Map<Reference<Block>, BlockColor> zeta$getBlockColors();

}
