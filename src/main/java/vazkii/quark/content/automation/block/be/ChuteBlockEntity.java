package vazkii.quark.content.automation.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import vazkii.zeta.block.be.ZetaBlockEntity;
import vazkii.quark.content.automation.block.ChuteBlock;
import vazkii.quark.content.automation.module.ChuteModule;
import vazkii.quark.content.building.module.GrateModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author WireSegal
 * Created at 10:18 AM on 9/29/19.
 */
public class ChuteBlockEntity extends ZetaBlockEntity {

	public ChuteBlockEntity(BlockPos pos, BlockState state) {
		super(ChuteModule.blockEntityType, pos, state);
	}

	private boolean canDropItem() {
		if(level != null && level.getBlockState(worldPosition).getValue(ChuteBlock.ENABLED)) {
			BlockPos below = worldPosition.below();
			BlockState state = level.getBlockState(below);
			return state.isAir() || state.getCollisionShape(level, below).isEmpty() || state.getBlock() == GrateModule.grate;
		}

		return false;
	}

	private final IItemHandler handler = new IItemHandler() {
		@Override
		public int getSlots() {
			return 1;
		}

		@Nonnull
		@Override
		public ItemStack getStackInSlot(int slot) {
			return ItemStack.EMPTY;
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (!canDropItem())
				return stack;

			if(!simulate && level != null && !stack.isEmpty()) {
				ItemEntity entity = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() - 0.5, worldPosition.getZ() + 0.5, stack.copy());
				entity.setDeltaMovement(0, 0, 0);
				level.addFreshEntity(entity);
			}

			return ItemStack.EMPTY;
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot) {
			return 64;
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			return true;
		}
	};

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (side != Direction.DOWN && cap == ForgeCapabilities.ITEM_HANDLER)
			return LazyOptional.of(() -> handler).cast();
		return super.getCapability(cap, side);
	}
}
