package org.violetmoon.quark.content.tweaks.module;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.play.ZPlayNoteBlock;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;

@ZetaLoadModule(category = "tweaks")
public class MoreNoteBlockSoundsModule extends ZetaModule {

	// this module *used* to have more config options
	@Config(flag = "amethyst_note_block")
	public static boolean enableAmethystSound = true;

	@Hint("amethyst_note_block")
	Item amethyst_block = Items.AMETHYST_BLOCK;

	@PlayEvent
	public void noteBlockPlayed(ZPlayNoteBlock event) {
		LevelAccessor world = event.getLevel();
		BlockPos pos = event.getPos();
		if(world.getBlockState(pos).getBlock() != Blocks.NOTE_BLOCK)
			return;

		if(enableAmethystSound && event.getInstrument() == NoteBlockInstrument.HARP &&
				world instanceof ServerLevel serverLevel && isAmethyst(world.getBlockState(pos.below()))) {
			event.setCanceled(true);
			int note = event.getState().getValue(NoteBlock.NOTE);
			float pitch = (float) Math.pow(2.0D, (double) (note - 12) / 12.0D);
			world.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.RECORDS, 1F, pitch);
			serverLevel.sendParticles(ParticleTypes.NOTE, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.2D, (double) pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0, (double) note / 24.0D);
		}
	}

	//TODO: make a tag?
	private boolean isAmethyst(BlockState state) {
		return state.getSoundType() == SoundType.AMETHYST ||
				state.getSoundType() == SoundType.AMETHYST_CLUSTER ||
				state.getBlock() == Blocks.LARGE_AMETHYST_BUD ||
				state.getBlock() == Blocks.MEDIUM_AMETHYST_BUD ||
				state.getBlock() == Blocks.SMALL_AMETHYST_BUD;
	}

}
