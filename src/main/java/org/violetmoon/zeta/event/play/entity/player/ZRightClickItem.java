package org.violetmoon.zeta.event.play.entity.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.violetmoon.zeta.event.bus.Cancellable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZRightClickItem extends IZetaPlayEvent, Cancellable {
	Player getEntity();
	ItemStack getItemStack();
	InteractionHand getHand();
	Level getLevel();

	void setCancellationResult(InteractionResult result);
}
