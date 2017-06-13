/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
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
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import vazkii.quark.base.lib.LibObfuscation;

public final class EmoteHandler {

	public static Map<String, EmoteDescriptor> emoteMap = new LinkedHashMap();
	private static WeakHashMap<EntityPlayer, EmoteBase> playerEmotes = new WeakHashMap();
	private static List<EntityPlayer> updatedPlayers = new ArrayList();

	private static int count;

	public static void addEmote(String name, Class<? extends EmoteBase> clazz) {
		EmoteDescriptor desc = new EmoteDescriptor(clazz, name, count++);
		emoteMap.put(name, desc);
	}
	
	public static void putEmote(AbstractClientPlayer player, String emoteName) {
		if(emoteMap.containsKey(emoteName))
			putEmote(player, emoteMap.get(emoteName));
	}

	public static void putEmote(AbstractClientPlayer player, EmoteDescriptor desc) {
		if(playerEmotes.containsKey(player))
			return;

		ModelBiped model = getPlayerModel(player);
		ModelBiped armorModel = getPlayerArmorModel(player);
		ModelBiped armorLegModel = getPlayerArmorLegModel(player);

		if(model.bipedHead.rotateAngleY < 0)
			model.bipedHead.rotateAngleY = 2 * (float) Math.PI - model.bipedHead.rotateAngleY;

		playerEmotes.put(player, desc.instantiate(player, model, armorModel, armorLegModel));
	}

	public static void updateEmotes(Entity e) {
		if(e instanceof AbstractClientPlayer) {
			AbstractClientPlayer player = (AbstractClientPlayer) e;

			if(playerEmotes.containsKey(player)) {
				EmoteBase emote = playerEmotes.get(player);
				boolean done = emote.isDone();

				if(player.swingProgress > 0 || player.hurtTime > 0)
					done = true;

				if(done) {
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
	
	public static EmoteBase getPlayerEmote(EntityPlayer player) {
		return playerEmotes.get(player);
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
		List list = ReflectionHelper.getPrivateValue(RenderLivingBase.class, getRenderPlayer(player), LibObfuscation.LAYER_RENDERERS);
		for(int i = 0; i < list.size(); i++)
			if(list.get(i) instanceof LayerBipedArmor)
				return ReflectionHelper.getPrivateValue(LayerArmorBase.class, (LayerArmorBase) list.get(i), LibObfuscation.MODEL_ARMOR);

		return null;
	}

	private static ModelBiped getPlayerArmorLegModel(AbstractClientPlayer player) {
		List list = ReflectionHelper.getPrivateValue(RenderLivingBase.class, getRenderPlayer(player), LibObfuscation.LAYER_RENDERERS);
		for(int i = 0; i < list.size(); i++)
			if(list.get(i) instanceof LayerBipedArmor)
				return ReflectionHelper.getPrivateValue(LayerArmorBase.class, (LayerArmorBase) list.get(i), LibObfuscation.MODEL_LEGGINGS);
		
		return null;
	}

	private static void resetModel(ModelBiped model) {
		model.bipedHead.rotateAngleZ = 0F;
		model.bipedHeadwear.rotateAngleZ = 0F;
		model.bipedBody.rotateAngleZ = 0F;
		model.bipedRightLeg.rotateAngleZ = 0F;
		model.bipedLeftLeg.rotateAngleZ = 0F;
	}

}
