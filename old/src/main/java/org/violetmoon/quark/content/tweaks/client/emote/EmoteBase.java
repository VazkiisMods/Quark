/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 21:37:06 (GMT)]
 */
package org.violetmoon.quark.content.tweaks.client.emote;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.player.Player;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenManager;

public abstract class EmoteBase {

	public static final float PI_F = (float) Math.PI;

	public final EmoteDescriptor desc;

	private final TweenManager emoteManager;
	private final HumanoidModel<?> model;
	private final HumanoidModel<?> armorModel;
	private final HumanoidModel<?> armorLegsModel;
	private final EmoteState state;
	private final Player player;

	public float timeDone, totalTime, animatedTime;
	private long lastMs;

	public EmoteBase(EmoteDescriptor desc, Player player, HumanoidModel<?> model, HumanoidModel<?> armorModel, HumanoidModel<?> armorLegsModel) {
		this.desc = desc;
		emoteManager = new TweenManager();
		state = new EmoteState(this);
		this.model = model;
		this.armorModel = armorModel;
		this.armorLegsModel = armorLegsModel;
		this.player = player;
	}

	public void startAllTimelines() {
		startTimeline(player, model);
		startTimeline(player, armorModel);
		startTimeline(player, armorLegsModel);
		lastMs = System.currentTimeMillis();
	}

	private void startTimeline(Player player, HumanoidModel<?> model) {
		Timeline timeline = getTimeline(player, model).start(emoteManager);
		totalTime = timeline.getFullDuration();
	}

	public abstract Timeline getTimeline(Player player, HumanoidModel<?> model);

	public abstract boolean usesBodyPart(int part);

	public void rotateAndOffset(PoseStack stack) {
		state.rotateAndOffset(stack, player);
	}

	public void update() {
		state.load(model);
		state.load(armorModel);
		state.load(armorLegsModel);

		long currTime = System.currentTimeMillis();
		long timeDiff = currTime - lastMs;
		animatedTime += timeDiff;
		emoteManager.update(timeDiff);
		state.save(model);

		lastMs = currTime;
		timeDone += timeDiff;
	}

	public boolean isDone() {
		return timeDone >= totalTime || player.attackAnim > 0 || player.hurtTime > 0;
	}

}
