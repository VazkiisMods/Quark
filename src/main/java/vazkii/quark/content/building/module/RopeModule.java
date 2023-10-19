package vazkii.quark.content.building.module;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.building.block.RopeBlock;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "building")
public class RopeModule extends QuarkModule {

	@Hint public static Block rope;

	@Config(description = "Set to true to allow ropes to move Tile Entities even if Pistons Push TEs is disabled.\nNote that ropes will still use the same blacklist.")
	public static boolean forceEnableMoveTileEntities = false;

	@Config
	public static boolean enableDispenserBehavior = true;

	@LoadEvent
	public final void register(ZRegister event) {
		rope = new RopeBlock("rope", this, CreativeModeTab.TAB_DECORATIONS,
				Block.Properties.of(Material.WOOL, MaterialColor.COLOR_BROWN)
						.strength(0.5f)
						.sound(SoundType.WOOL));
	}

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		if(enableDispenserBehavior)
			DispenserBlock.DISPENSER_REGISTRY.put(rope.asItem(), new BehaviourRope());
		else
			DispenserBlock.DISPENSER_REGISTRY.remove(rope.asItem());
	}

	public static class BehaviourRope extends OptionalDispenseItemBehavior {

		@Nonnull
		@Override
		protected ItemStack execute(BlockSource source, @Nonnull ItemStack stack) {
			Direction facing = source.getBlockState().getValue(DispenserBlock.FACING);
			BlockPos pos = source.getPos().relative(facing);
			Level world = source.getLevel();
			setSuccess(false);

			BlockState state = world.getBlockState(pos);
			if(state.getBlock() == rope) {
				if(((RopeBlock) rope).pullDown(world, pos)) {
					setSuccess(true);
					stack.shrink(1);
					return stack;
				}
			} else if(world.isEmptyBlock(pos) && rope.defaultBlockState().canSurvive(world, pos)) {
				SoundType soundtype = rope.getSoundType(state, world, pos, null);
				world.setBlockAndUpdate(pos, rope.defaultBlockState());
				world.playSound(null, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				setSuccess(true);
				stack.shrink(1);

				return stack;
			}

			return stack;
		}

	}

}
