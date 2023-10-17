package vazkii.zetaimplforge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.zeta.Zeta;
import vazkii.zetaimplforge.event.ForgeZPlayNoteBlock;

public class ForgePlayEventBusPassage {
	public ForgePlayEventBusPassage(Zeta z) {
		this.z = z;

		MinecraftForge.EVENT_BUS.addListener(this::playNoteBlock);
	}

	private final Zeta z;

	@SubscribeEvent
	public void playNoteBlock(NoteBlockEvent.Play e) {
		z.playBus.fire(new ForgeZPlayNoteBlock(e));
	}
}
