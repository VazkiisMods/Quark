package org.violetmoon.quark.content.tools.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.quark.content.tools.block.be.CloudBlockEntity;
import org.violetmoon.quark.content.tools.module.BottledCloudModule;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;

public class CloudBlock extends QuarkBlock implements EntityBlock {

	public CloudBlock(ZetaModule module) {
		super("cloud", module, null,
				Block.Properties.of(Material.CLAY)
				.sound(SoundType.WOOL)
				.strength(0)
				.noOcclusion()
				.noCollission());
	}

	@Nonnull
	@Override
	public PushReaction getPistonPushReaction(@Nonnull BlockState state) {
		return PushReaction.BLOCK;
	}

	@Nonnull
	@Override
	public InteractionResult use(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult raytrace) {
		ItemStack stack = player.getItemInHand(hand);

		if(stack.getItem() == Items.GLASS_BOTTLE) {
			fillBottle(player, player.getInventory().selected);
			world.removeBlock(pos, false);
			return InteractionResult.SUCCESS;
		}

		if(stack.getItem() instanceof BlockItem bitem) {
			Block block = bitem.getBlock();

			UseOnContext context = new UseOnContext(player, hand, new BlockHitResult(new Vec3(0.5F, 1F, 0.5F), raytrace.getDirection(), pos, false));
			BlockPlaceContext bcontext = new BlockPlaceContext(context);

			BlockState stateToPlace = block.getStateForPlacement(bcontext);
			if(stateToPlace != null && stateToPlace.canSurvive(world, pos)) {
				world.setBlockAndUpdate(pos, stateToPlace);
				world.playSound(player, pos, stateToPlace.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1F, 1F);

				if(!player.getAbilities().instabuild) {
					stack.shrink(1);
					fillBottle(player, 0);
				}

				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@SuppressWarnings("deprecation") //Needless Forge extension
	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		return new ItemStack(BottledCloudModule.bottled_cloud);
	}

	private void fillBottle(Player player, int startIndex) {
		Inventory inv = player.getInventory();
		for(int i = startIndex ; i < inv.getContainerSize(); i++) {
			ItemStack stackInSlot = inv.getItem(i);
			if(stackInSlot.getItem() == Items.GLASS_BOTTLE) {
				stackInSlot.shrink(1);

				ItemStack give = new ItemStack(BottledCloudModule.bottled_cloud);
				if(!player.addItem(give))
					player.drop(give, false);
				return;
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
		return new CloudBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
		return createTickerHelper(type, BottledCloudModule.blockEntityType, CloudBlockEntity::tick);
	}

}
