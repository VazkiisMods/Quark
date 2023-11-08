package org.violetmoon.quark.content.tools.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.violetmoon.quark.base.handler.RayTraceHandler;
import org.violetmoon.quark.base.item.QuarkItem;
import org.violetmoon.quark.content.tools.module.BottledCloudModule;
import org.violetmoon.quark.integration.claim.IClaimIntegration;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;

public class BottledCloudItem extends QuarkItem {

	public BottledCloudItem(ZetaModule module) {
		super("bottled_cloud", module, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(@Nonnull Level world, Player player, @Nonnull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		HitResult result = RayTraceHandler.rayTrace(player, world, player, Block.OUTLINE, Fluid.ANY);
		if(result instanceof BlockHitResult bresult) {
			BlockPos pos = bresult.getBlockPos();
			if(!world.isEmptyBlock(pos))
				pos = pos.relative(bresult.getDirection());

			if(world.isEmptyBlock(pos) && IClaimIntegration.INSTANCE.canPlace(player, pos)) {

				if(!world.isClientSide) {
					world.gameEvent(player, GameEvent.BLOCK_PLACE, pos);
					world.setBlockAndUpdate(pos, BottledCloudModule.cloud.defaultBlockState());
				}

				stack.shrink(1);

				if(!player.getAbilities().instabuild) {
					ItemStack returnStack = new ItemStack(Items.GLASS_BOTTLE);
					if(stack.isEmpty())
						stack = returnStack;
					else if(!player.addItem(returnStack))
						player.drop(returnStack, false);
				}

				player.getCooldowns().addCooldown(this, 10);
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
			}
		}

		return new InteractionResultHolder<>(InteractionResult.PASS, stack);
	}

}
