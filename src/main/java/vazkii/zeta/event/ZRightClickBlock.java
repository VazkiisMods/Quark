package vazkii.zeta.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import vazkii.zeta.event.bus.Cancellable;
import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.Resultable;
import vazkii.zeta.event.bus.ZResult;

public interface ZRightClickBlock extends IZetaPlayEvent, Cancellable, Resultable {
	Player getEntity();
	Level getLevel();
	BlockPos getPos();

	ZResult getUseBlock();

	interface Low extends IZetaPlayEvent, ZRightClickBlock { }
}
