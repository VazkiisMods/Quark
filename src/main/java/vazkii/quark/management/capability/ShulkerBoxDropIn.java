package vazkii.quark.management.capability;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import vazkii.arl.util.AbstractDropIn;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.handler.SimilarBlockTypeHandler;

public class ShulkerBoxDropIn extends AbstractDropIn {

	@Override
	public boolean canDropItemIn(PlayerEntity player, ItemStack stack, ItemStack incoming) {
		return tryAddToShulkerBox(stack, incoming, true);
	}

	@Override
	public ItemStack dropItemIn(PlayerEntity player, ItemStack stack, ItemStack incoming) {
		tryAddToShulkerBox(stack, incoming, false);
		return stack;
	}
	
	private boolean tryAddToShulkerBox(ItemStack shulkerBox, ItemStack stack, boolean simulate) {
		if (!SimilarBlockTypeHandler.isShulkerBox(shulkerBox))
			return false;

		CompoundNBT cmp = ItemNBTHelper.getCompound(shulkerBox, "BlockEntityTag", false);
		if (cmp != null) {
			TileEntity te = null;

			if (shulkerBox.getItem() instanceof BlockItem) {
				Block shulkerBoxBlock = Block.getBlockFromItem(shulkerBox.getItem());
				if (shulkerBoxBlock.hasTileEntity()) {
					te = shulkerBoxBlock.createTileEntity(null, null);
					te.read(cmp);
				}
			}

			if (te != null) {
				LazyOptional<IItemHandler> handlerHolder = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				if (handlerHolder.isPresent()) {
					IItemHandler handler = handlerHolder.orElseGet(EmptyHandler::new);
					ItemStack result = ItemHandlerHelper.insertItem(handler, stack.copy(), simulate);
					boolean did = result.isEmpty() || result.getCount() != stack.getCount();

					if (!simulate && did) {
						stack.setCount(result.getCount());
						te.write(cmp);
						ItemNBTHelper.setCompound(shulkerBox, "BlockEntityTag", cmp);
					}

					return did;
				}
			}
		}

		return false;
	}
	
}
