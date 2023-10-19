package vazkii.zetaimplforge.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.event.level.NoteBlockEvent;
import vazkii.zeta.event.ZPlayNoteBlock;
import vazkii.zeta.event.bus.FiredAs;

@FiredAs(ZPlayNoteBlock.class)
public record ForgeZPlayNoteBlock(NoteBlockEvent.Play e) implements ZPlayNoteBlock {
	@Override
	public LevelAccessor getLevel() {
		return e.getLevel();
	}

	@Override
	public BlockPos getPos() {
		return e.getPos();
	}

	@Override
	public BlockState getState() {
		return e.getState();
	}

	@Override
	public int getVanillaNoteId() {
		return e.getVanillaNoteId();
	}

	@Override
	public NoteBlockInstrument getInstrument() {
		return e.getInstrument();
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
