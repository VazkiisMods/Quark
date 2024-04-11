/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 05, 2019, 21:30 AM (EST)]
 */
package org.violetmoon.quark.content.tweaks.client.emote;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.lang.ref.WeakReference;
import java.util.List;

public class EmoteSound extends AbstractSoundInstance implements TickableSoundInstance {

	protected boolean donePlaying;

	private final WeakReference<Player> player;
	private final EmoteTemplate template;
	private final boolean endWithSequence;

	public static void add(List<EmoteSound> allSounds, List<EmoteSound> sounds, Player player, EmoteTemplate template,
			ResourceLocation soundEvent, float volume, float pitch,
			boolean repeating, boolean endWithSequence) {
		EmoteSound emoteSound = new EmoteSound(player, template, soundEvent, volume, pitch, repeating, endWithSequence);
		sounds.add(emoteSound);
		allSounds.add(emoteSound);
		Minecraft.getInstance().getSoundManager().play(emoteSound);
	}

	public static void endAll(List<EmoteSound> sounds) {
		for(EmoteSound sound : sounds)
			sound.donePlaying = true;
	}

	public static void endSection(List<EmoteSound> sounds) {
		for(EmoteSound sound : sounds)
			if(sound.endWithSequence)
				sound.donePlaying = true;
	}

	public EmoteSound(Player player, EmoteTemplate template, ResourceLocation sound, float volume, float pitch, boolean repeating, boolean endWithSequence) {
		super(sound, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
		this.player = new WeakReference<>(player);
		this.template = template;
		this.endWithSequence = endWithSequence;

		this.volume = volume;
		this.pitch = pitch;

		if(repeating) {
			this.looping = true;
			this.delay = 0;
		}
	}

	@Override
	public void tick() {
		Player player = this.player.get();

		if(player == null || !player.isAlive())
			donePlaying = true;
		else {
			EmoteBase emote = EmoteHandler.getPlayerEmote(player);
			if(emote == null || emote.desc.template != template)
				donePlaying = true;
			else {
				Vec3 pos = player.position();
				x = (float) pos.x;
				y = (float) pos.y;
				z = (float) pos.z;
			}
		}
	}

	@Override
	public boolean isStopped() {
		return this.donePlaying;
	}
}
