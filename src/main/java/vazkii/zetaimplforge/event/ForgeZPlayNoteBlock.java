package vazkii.zetaimplforge.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.event.level.NoteBlockEvent;
import vazkii.zeta.event.ZPlayNoteBlock;

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
	public boolean isCancelled() {
		return e.isCanceled();
	}

	@Override
	public void cancel() {
		e.setCanceled(true);
	}
}
