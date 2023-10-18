package vazkii.quark.content.tweaks.module;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;

import java.util.Arrays;
import java.util.List;

@LoadModule(category = "tweaks", hasSubscriptions = true)
public class MoreNoteBlockSoundsModule extends QuarkModule {

	@Config(flag = "skull_note_block")
	public static boolean enableSkullSounds = true;
	@Config(flag = "amethyst_note_block")
	public static boolean enableAmethystSound = true;

	@Hint("amethyst_note_block") Item amethyst_block = Items.AMETHYST_BLOCK;
	@Hint(value = "skull_note_block", key = "head_sfx")
	List<Item> skulls = Arrays.asList(Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.ZOMBIE_HEAD, Items.CREEPER_HEAD, Items.DRAGON_HEAD);

	@SubscribeEvent
	public void noteBlockPlayed(NoteBlockEvent.Play event) {
		LevelAccessor world = event.getLevel();
		BlockPos pos = event.getPos();
		if(world.getBlockState(pos).getBlock() != Blocks.NOTE_BLOCK)
			return;


		if (enableSkullSounds) {
			SoundEvent sound = null;
			for (Direction dir : MiscUtil.HORIZONTALS) {
				sound = getSoundEvent(world, pos, dir);
				if (sound != null)
					break;
			}

			if (sound != null) {
				event.setCanceled(true);

				float pitch = (float) Math.pow(2.0, (event.getVanillaNoteId() - 12) / 12.0);
				world.playSound(null, pos.above(), sound, SoundSource.BLOCKS, 1F, pitch);

				return;
			}
		}

		if (enableAmethystSound && event.getInstrument() == NoteBlockInstrument.HARP &&
			 world instanceof ServerLevel serverLevel && world.getBlockState(pos.below()).getMaterial() == Material.AMETHYST) {
			event.setCanceled(true);
			int note = event.getState().getValue(NoteBlock.NOTE);
			float pitch = (float) Math.pow(2.0D, (double) (note - 12) / 12.0D);
			world.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.RECORDS, 1F, pitch);
			serverLevel.sendParticles(ParticleTypes.NOTE, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.2D, (double) pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0, (double) note / 24.0D);
		}
	}

	public SoundEvent getSoundEvent(LevelAccessor world, BlockPos pos, Direction direction) {
		BlockState state = world.getBlockState(pos.relative(direction));
		Block block = state.getBlock();

		if(block instanceof WallSkullBlock && state.getValue(WallSkullBlock.FACING) == direction) {
			if(block == Blocks.SKELETON_WALL_SKULL)
				return SoundEvents.SKELETON_AMBIENT;
			else if(block == Blocks.WITHER_SKELETON_WALL_SKULL)
				return SoundEvents.WITHER_SKELETON_AMBIENT;
			else if(block == Blocks.ZOMBIE_WALL_HEAD)
				return SoundEvents.ZOMBIE_AMBIENT;
			else if(block == Blocks.CREEPER_WALL_HEAD)
				return SoundEvents.CREEPER_PRIMED;
			else if(block == Blocks.DRAGON_WALL_HEAD)
				return SoundEvents.ENDER_DRAGON_AMBIENT;
		}

		return null;
	}

}
