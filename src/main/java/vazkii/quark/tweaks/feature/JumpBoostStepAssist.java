/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [23/03/2016, 23:13:57 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import vazkii.quark.base.module.Feature;

public class JumpBoostStepAssist extends Feature {

	public static List<String> playersWithStepup = new ArrayList();

	int minimumLevel;
	boolean canToggleWithSneak;

	@Override
	public void setupConfig() {
		minimumLevel = loadPropInt("Minimum Jump Boost level", "", 2);
		canToggleWithSneak = loadPropBool("Can toggle with sneaking", "", true);

		minimumLevel--;
	}

	@SubscribeEvent
	public void updatePlayerStepStatus(LivingUpdateEvent event) {
		if(event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			String s = playerStr(player);

			if(playersWithStepup.contains(s)) {
				if(shouldPlayerHaveStepup(player)) {
					if(canToggleWithSneak && player.isSneaking())
						player.stepHeight = 0.50001F; // Not 0.5F because that is the default
					else player.stepHeight = 1.1875F;
				} else {
					player.stepHeight = 0.5F;
					playersWithStepup.remove(s);
				}
			} else if(shouldPlayerHaveStepup(player)) {
				playersWithStepup.add(s);
				player.stepHeight = 1.1875F;
			}
		}
	}

	private boolean shouldPlayerHaveStepup(EntityPlayer player) {
		PotionEffect jumpBoost = player.getActivePotionEffect(MobEffects.JUMP_BOOST);
		return jumpBoost != null && jumpBoost.getAmplifier() >= minimumLevel;
	}

	@SubscribeEvent
	public void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		String username = event.player.getGameProfile().getName();
		playersWithStepup.remove(username + ":false");
		playersWithStepup.remove(username + ":true");
	}

	public static String playerStr(EntityPlayer player) {
		return player.getGameProfile().getName() + ":" + player.getEntityWorld().isRemote;
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
