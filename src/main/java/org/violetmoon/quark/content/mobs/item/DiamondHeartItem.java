package org.violetmoon.quark.content.mobs.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.violetmoon.quark.base.item.QuarkItem;
import org.violetmoon.quark.content.mobs.entity.Stoneling;
import org.violetmoon.quark.content.mobs.entity.Stoneling.StonelingVariant;
import org.violetmoon.quark.content.mobs.module.StonelingsModule;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;

public class DiamondHeartItem extends QuarkItem {

	public DiamondHeartItem(String regname, ZetaModule module, Properties properties) {
		super(regname, module, properties);
	}

	@Nonnull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		InteractionHand hand = context.getHand();
		Direction facing = context.getClickedFace();

		if (player != null) {
			BlockState stateAt = world.getBlockState(pos);
			ItemStack stack = player.getItemInHand(hand);

			if (player.mayUseItemAt(pos, facing, stack) && stateAt.getDestroySpeed(world, pos) != -1) {

				StonelingVariant variant = null;
				for (StonelingVariant possibleVariant : StonelingVariant.values()) {
					if (possibleVariant.getBlocks().contains(stateAt.getBlock()))
						variant = possibleVariant;
				}

				if (variant != null) {
					if (!world.isClientSide && world instanceof ServerLevelAccessor serverLevel) {
						world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
						world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(stateAt));

						Stoneling stoneling = new Stoneling(StonelingsModule.stonelingType, world);
						stoneling.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
						stoneling.setPlayerMade(true);
						stoneling.setYRot(player.getYRot() + 180F);
						stoneling.finalizeSpawn(serverLevel, world.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, variant, null);
						world.addFreshEntity(stoneling);

						if(player instanceof ServerPlayer serverPlayer) {
							CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, stoneling);
							StonelingsModule.makeStonelingTrigger.trigger(serverPlayer);
						}

						if (!player.getAbilities().instabuild)
							stack.shrink(1);
					}

					return InteractionResult.SUCCESS;
				}
			}
		}

		return InteractionResult.PASS;
	}

	@Nonnull
	@Override
	public Rarity getRarity(@Nonnull ItemStack stack) {
		return Rarity.UNCOMMON;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(@Nonnull ItemStack stack) {
		return true;
	}

}
