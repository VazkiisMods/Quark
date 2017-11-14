package vazkii.quark.base.network.message;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityNote;
import vazkii.arl.network.TileEntityMessage;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.misc.feature.NoteBlockInterface;

public class MessageTuneNoteBlock extends TileEntityMessage<TileEntityNote> {

	public boolean next;
	public byte target;

	public MessageTuneNoteBlock() { }

	public MessageTuneNoteBlock(TileEntityNote note, boolean next, byte target) {
		super(note.getPos());
		this.next = next;
		this.target = target;
	}

	@Override
	public Runnable getAction() {
		return () -> {
			if(!ModuleLoader.isFeatureEnabled(NoteBlockInterface.class))
				return;
			
			EntityPlayer player = context.getServerHandler().player;
			byte old = tile.note;
			boolean tuned = false;
			
			if(next)
				tile.changePitch();
			else {
				tile.note = target;
				if(net.minecraftforge.common.ForgeHooks.onNoteChange(tile, old)) {
					tile.markDirty();
					player.addStat(StatList.NOTEBLOCK_TUNED);
					tuned = true;
				}
			}

			if(!tuned)
				player.addStat(StatList.NOTEBLOCK_PLAYED);
			
			tile.triggerNote(tile.getWorld(), pos);
		};
	}

}
