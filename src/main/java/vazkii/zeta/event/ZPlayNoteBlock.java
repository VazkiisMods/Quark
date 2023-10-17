package vazkii.zeta.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import vazkii.zeta.event.bus.Cancellable;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZPlayNoteBlock extends IZetaPlayEvent, Cancellable {
	LevelAccessor getLevel();
	BlockPos getPos();
	BlockState getState();

	int getVanillaNoteId();
	NoteBlockInstrument getInstrument();
}
