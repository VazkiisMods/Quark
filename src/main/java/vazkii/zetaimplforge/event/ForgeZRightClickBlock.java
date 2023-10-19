package vazkii.zetaimplforge.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import vazkii.zeta.event.ZRightClickBlock;
import vazkii.zeta.event.bus.FiredAs;
import vazkii.zeta.event.bus.ZResult;
import vazkii.zetaimplforge.ForgeZeta;

@FiredAs(ZRightClickBlock.class)
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
	public ZResult getUseBlock() {
		return ForgeZeta.from(e.getUseBlock());
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

	@FiredAs(ZRightClickBlock.Low.class)
	public static class Low extends ForgeZRightClickBlock implements ZRightClickBlock.Low {
		public Low(PlayerInteractEvent.RightClickBlock e) {
			super(e);
		}
	}
}
