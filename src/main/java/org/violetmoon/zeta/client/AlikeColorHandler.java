package org.violetmoon.zeta.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * A BlockColor and ItemColor that just mimics another block.
 */
public class AlikeColorHandler implements BlockColor, ItemColor {
	private final BlockState likeState;
	private final ItemStack likeItem;

	public AlikeColorHandler(BlockState likeState, ItemStack likeItem) {
		this.likeState = likeState;
		this.likeItem = likeItem;
	}

	public AlikeColorHandler(BlockState likeState) {
		this(likeState, new ItemStack(likeState.getBlock()));
	}

	/**
	 * construction convenience, for when the BlockState to mimic is derived from the block
	 */
	public <B extends Block> AlikeColorHandler(B block, Function<B, BlockState> stateExtractor) {
		this.likeState = stateExtractor.apply(block);
		this.likeItem = new ItemStack(likeState.getBlock());
	}

	@SuppressWarnings("unchecked")
	public <B extends Block> AlikeColorHandler(Item item, Function<B, BlockState> stateExtractor) {
		this((B) Block.byItem(item), stateExtractor);
	}

	@Override
	public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
		return Minecraft.getInstance().getBlockColors().getColor(likeState, level, pos, tintIndex);
	}

	@Override
	public int getColor(ItemStack stack, int tintIndex) {
		return Minecraft.getInstance().getItemColors().getColor(likeItem, tintIndex);
	}
}
