/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [26/03/2016, 21:37:06 (GMT)]
 */
package vazkii.quark.vanity.client.emotes.base;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import vazkii.aurelienribon.tweenengine.BaseTween;
import vazkii.aurelienribon.tweenengine.Timeline;
import vazkii.aurelienribon.tweenengine.TweenCallback;
import vazkii.aurelienribon.tweenengine.TweenManager;

public abstract class EmoteBase {

	public static final float PI_F = (float) Math.PI;

	public TweenManager emoteManager;
	private ModelBiped model;
	private ModelBiped armorModel;
	private ModelBiped armorLegsModel;
	private EmoteState state;

	private boolean done = false;

	public EmoteBase(EntityPlayer player, ModelBiped model, ModelBiped armorModel, ModelBiped armorLegsModel) {
		emoteManager = new TweenManager();
		state = new EmoteState(this);
		this.model = model;
		this.armorModel = armorModel;
		this.armorLegsModel = armorLegsModel;

		startTimeline(player, model, true);
		startTimeline(player, armorModel, false);
		startTimeline(player, armorLegsModel, false);
	}

	void startTimeline(EntityPlayer player, ModelBiped model, boolean callback) {
		Timeline timeline = getTimeline(player, model).start(emoteManager);
		if(callback)
			timeline.setCallback(new FinishCallback());
	}

	public abstract Timeline getTimeline(EntityPlayer player, ModelBiped model);

	public abstract boolean usesBodyPart(int part);

	public void update(boolean doUpdate) {
		state.load(model);
		state.load(armorModel);
		state.load(armorLegsModel);
		if(doUpdate) {
			emoteManager.update(EmoteHandler.delta);
			state.save(model);
		}
	}

	public boolean isDone() {
		return done;
	}

	private class FinishCallback implements TweenCallback {

		@Override
		public void onEvent(int type, BaseTween<?> source) {
			if(type == COMPLETE)
				done = true;
		}

	}

}
