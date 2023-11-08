package org.violetmoon.quark.content.building.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.violetmoon.quark.base.item.QuarkItem;
import org.violetmoon.quark.base.util.TriFunction;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;

/**
 * @author WireSegal
 * Created at 11:04 AM on 8/25/19.
 */
public class QuarkItemFrameItem extends QuarkItem {
	private final TriFunction<? extends HangingEntity, Level, BlockPos, Direction> entityProvider;

	public QuarkItemFrameItem(String name, ZetaModule module, TriFunction<? extends HangingEntity, Level, BlockPos, Direction> entityProvider) {
		super(name, module, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS));
		this.entityProvider = entityProvider;
	}

	@Nonnull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		BlockPos pos = context.getClickedPos();
		Direction facing = context.getClickedFace();
		BlockPos placeLocation = pos.relative(facing);
		Player player = context.getPlayer();
		ItemStack stack = context.getItemInHand();
		if (player != null && !this.canPlace(player, facing, stack, placeLocation)) {
			return InteractionResult.FAIL;
		} else {
			Level world = context.getLevel();
			HangingEntity frame = entityProvider.apply(world, placeLocation, facing);

			CompoundTag tag = stack.getTag();
			if (tag != null)
				EntityType.updateCustomEntityTag(world, player, frame, tag);

			if (frame.survives()) {
				if (!world.isClientSide) {
					world.gameEvent(player, GameEvent.ENTITY_PLACE, frame.position());

					frame.playPlacementSound();
					world.addFreshEntity(frame);
				}

				stack.shrink(1);
			}

			return InteractionResult.SUCCESS;
		}
	}

	protected boolean canPlace(Player player, Direction facing, ItemStack stack, BlockPos pos) {
		return !player.level.isOutsideBuildHeight(pos) && player.mayUseItemAt(pos, facing, stack);
	}
}
