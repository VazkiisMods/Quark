package org.violetmoon.zetaimplforge.event.play.entity.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.violetmoon.zeta.event.play.entity.player.ZRightClickItem;

public class ForgeZRightClickItem implements ZRightClickItem {
	private final PlayerInteractEvent.RightClickItem e;

	public ForgeZRightClickItem(PlayerInteractEvent.RightClickItem e) {
		this.e = e;
	}

	@Override
	public Player getEntity() {
		return e.getEntity();
	}

	@Override
	public ItemStack getItemStack() {
		return e.getItemStack();
	}

	@Override
	public InteractionHand getHand() {
		return e.getHand();
	}

	@Override
	public Level getLevel() {
		return e.getLevel();
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
}
