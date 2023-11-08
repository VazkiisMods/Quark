package org.violetmoon.zetaimplforge.event.play.entity.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.violetmoon.zeta.event.bus.ZResult;
import org.violetmoon.zeta.event.play.entity.player.ZRightClickBlock;
import org.violetmoon.zetaimplforge.ForgeZeta;

public class ForgeZRightClickBlock implements ZRightClickBlock {
	private final PlayerInteractEvent.RightClickBlock e;

	public ForgeZRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
		this.e = e;
	}

	@Override
	public Player getEntity() {
		return e.getEntity();
	}

	@Override
	public Level getLevel() {
		return e.getLevel();
	}

	@Override
	public BlockPos getPos() {
		return e.getPos();
	}

	@Override
	public InteractionHand getHand() {
		return e.getHand();
	}

	@Override
	public ItemStack getItemStack() {
		return e.getItemStack();
	}

	@Override
	public BlockHitResult getHitVec() {
		return e.getHitVec();
	}

	@Override
	public Direction getFace() {
		return e.getFace();
	}

	@Override
	public ZResult getUseBlock() {
		return ForgeZeta.from(e.getUseBlock());
	}

	@Override
	public void setCancellationResult(InteractionResult result) {
		e.setCancellationResult(result);
	}

	@Override
	public boolean isCanceled() {
		return e.isCanceled();
	}

	@Override
	public void setCanceled(boolean cancel) {
		e.setCanceled(cancel);
	}

	@Override
	public ZResult getResult() {
		return ForgeZeta.from(e.getResult());
	}

	@Override
	public void setResult(ZResult value) {
		e.setResult(ForgeZeta.to(value));
	}

	public static class Low extends ForgeZRightClickBlock implements ZRightClickBlock.Low {
		public Low(PlayerInteractEvent.RightClickBlock e) {
			super(e);
		}
	}
}
