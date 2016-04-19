/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [26/03/2016, 21:37:17 (GMT)]
 */
package vazkii.quark.vanity.client.emotes.base;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import vazkii.quark.base.lib.LibObfuscation;

public final class EmoteHandler {

	public static Map<String, Class<? extends EmoteBase>> emoteMap = new LinkedHashMap();
	private static WeakHashMap<EntityPlayer, EmoteBase> playerEmotes = new WeakHashMap();
	private static List<EntityPlayer> updatedPlayers = new ArrayList();

	public static float time, partialTicks, total, delta;

	public static void putEmote(AbstractClientPlayer player, String emoteName) {
		if(emoteMap.containsKey(emoteName))
			putEmote(player, emoteMap.get(emoteName));
	}

	public static void putEmote(AbstractClientPlayer player, Class<? extends EmoteBase> clazz) {
		ModelBiped model = getPlayerModel(player);
		ModelBiped armorModel = getPlayerArmorModel(player);
		ModelBiped armorLegModel = getPlayerArmorLegModel(player);

		if(model.bipedHead.rotateAngleY < 0)
			model.bipedHead.rotateAngleY = 2 * (float) Math.PI - model.bipedHead.rotateAngleY;

		try {
			playerEmotes.put(player, clazz.getConstructor(EntityPlayer.class, ModelBiped.class, ModelBiped.class, ModelBiped.class).newInstance(player, model, armorModel, armorLegModel));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateEmotes(Entity e) {
		if(e instanceof AbstractClientPlayer) {
			AbstractClientPlayer player = (AbstractClientPlayer) e;

			if(playerEmotes.containsKey(player)) {
				EmoteBase emote = playerEmotes.get(player);
				if(emote.isDone()) {
					playerEmotes.remove(player);
					resetModel(getPlayerModel(player));
					resetModel(getPlayerArmorModel(player));
					resetModel(getPlayerArmorLegModel(player));
				}
				else emote.update(!updatedPlayers.contains(player));
				updatedPlayers.add(player);
			}
		}
	}

	public static void clearPlayerList() {
		updatedPlayers.clear();
	}

	private static RenderPlayer getRenderPlayer(AbstractClientPlayer player) {
		Minecraft mc = Minecraft.getMinecraft();
		RenderManager manager = mc.getRenderManager();
		return manager.getSkinMap().get(player.getSkinType());
	}

	private static ModelBiped getPlayerModel(AbstractClientPlayer player) {
		return getRenderPlayer(player).getMainModel();
	}

	private static ModelBiped getPlayerArmorModel(AbstractClientPlayer player) {
		List<LayerRenderer> list = ReflectionHelper.getPrivateValue(RenderLivingBase.class, getRenderPlayer(player), LibObfuscation.LAYER_RENDERERS);
		return ReflectionHelper.getPrivateValue(LayerArmorBase.class, (LayerArmorBase) list.get(0), LibObfuscation.MODEL_ARMOR);
	}

	private static ModelBiped getPlayerArmorLegModel(AbstractClientPlayer player) {
		List<LayerRenderer> list = ReflectionHelper.getPrivateValue(RenderLivingBase.class, getRenderPlayer(player), LibObfuscation.LAYER_RENDERERS);
		return ReflectionHelper.getPrivateValue(LayerArmorBase.class, (LayerArmorBase) list.get(0), LibObfuscation.MODEL_LEGGINGS);
	}

	private static void resetModel(ModelBiped model) {
		model.bipedHead.rotateAngleZ = 0F;
		model.bipedHeadwear.rotateAngleZ = 0F;
		model.bipedBody.rotateAngleZ = 0F;
		model.bipedRightLeg.rotateAngleZ = 0F;
		model.bipedLeftLeg.rotateAngleZ = 0F;
	}

	public static class TickHandler {

		private void calcDelta() {
			float oldTotal = total;
			total = time + partialTicks;
			delta = (total - oldTotal) * 50F;
		}

		@SubscribeEvent
		public void renderTick(RenderTickEvent event) {
			if(event.phase == Phase.START) {
				partialTicks = event.renderTickTime;
				EmoteHandler.clearPlayerList();
			} else calcDelta();
		}

		@SubscribeEvent
		public void clientTickEnd(ClientTickEvent event) {
			if(event.phase == Phase.END) {
				Minecraft mc = Minecraft.getMinecraft();

				GuiScreen gui = mc.currentScreen;
				if(gui == null || !gui.doesGuiPauseGame()) {
					time++;
					partialTicks = 0;
				}

				calcDelta();
			}
		}
	}

}
