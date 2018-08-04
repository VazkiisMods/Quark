package vazkii.quark.experimental.features;

import com.mojang.text2speech.Narrator;

import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class ParrotsSayDespacito extends Feature {

	@SubscribeEvent
	public void onInteract(EntityInteract event) {
		if(event.getTarget() instanceof EntityParrot && event.getWorld().isRemote && event.getHand() == EnumHand.MAIN_HAND) {
			Narrator narrator = Narrator.getNarrator();
			narrator.say("Deh Spa See Tow");
		}
	}
	
	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}
	
}
