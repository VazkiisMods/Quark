package vazkii.quark.content.automation.module;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import vazkii.quark.base.Quark;
import vazkii.zeta.event.ZAttachCapabilities;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

import javax.annotation.Nonnull;

@ZetaLoadModule(category = "automation")
public class JukeboxAutomationModule extends ZetaModule {

	@Hint Item jukebox = Items.JUKEBOX;

	private static final ResourceLocation JUKEBOX_ITEM_HANDLER = new ResourceLocation(Quark.MOD_ID, "jukebox_item_handler");

	@LoadEvent
	public void setup(ZCommonSetup e) {
		MusicDiscBehaviour behaviour = new MusicDiscBehaviour();
		e.enqueueWork(() -> Registry.ITEM.forEach(i -> {
			if (i instanceof RecordItem)
				DispenserBlock.DISPENSER_REGISTRY.put(i, behaviour);
		}));
	}

	@PlayEvent
	public void attachCaps(ZAttachCapabilities.BlockEntityCaps event) {
		if(event.getObject() instanceof JukeboxBlockEntity jukebox)
			event.addCapabilityForgeApi(JUKEBOX_ITEM_HANDLER, new JukeboxItemHandler(jukebox));
	}

	public record JukeboxItemHandler(JukeboxBlockEntity tile) implements ICapabilityProvider, IItemHandler {

		@Override
		public int getSlots() {
			return 1;
		}

		@Nonnull
		@Override
		public ItemStack getStackInSlot(int slot) {
			return tile.getRecord();
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return stack;
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			ItemStack stackAt = getStackInSlot(slot);
			if (!stackAt.isEmpty()) {
				ItemStack copy = stackAt.copy();
				if (!simulate) {
					tile.getLevel().levelEvent(LevelEvent.SOUND_PLAY_RECORDING, tile.getBlockPos(), 0);
					tile.setRecord(ItemStack.EMPTY);

					BlockState state = tile.getBlockState().setValue(JukeboxBlock.HAS_RECORD, false);
					tile.getLevel().setBlock(tile.getBlockPos(), state, 1 | 2);
				}

				return copy;
			}

			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			return false;
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
			if (cap == ForgeCapabilities.ITEM_HANDLER)
				return LazyOptional.of(() -> this).cast();

			return LazyOptional.empty();
		}

	}

	public static class MusicDiscBehaviour extends OptionalDispenseItemBehavior {

		@Nonnull
		@Override
		protected ItemStack execute(BlockSource source, @Nonnull ItemStack stack) {
			Direction dir = source.getBlockState().getValue(DispenserBlock.FACING);
			BlockPos pos = source.getPos().relative(dir);
			Level world = source.getLevel();
			BlockState state = world.getBlockState(pos);

			if(state.getBlock() == Blocks.JUKEBOX) {
				JukeboxBlockEntity jukebox = (JukeboxBlockEntity) world.getBlockEntity(pos);
				if (jukebox != null) {
					ItemStack currentRecord = jukebox.getRecord();
					((JukeboxBlock) state.getBlock()).setRecord(null, world, pos, state, stack);
					world.levelEvent(null, LevelEvent.SOUND_PLAY_RECORDING, pos, Item.getId(stack.getItem()));

					return currentRecord;
				}
			}

			return super.execute(source, stack);
		}

	}

}
