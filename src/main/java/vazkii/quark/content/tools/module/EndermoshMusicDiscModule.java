package vazkii.quark.content.tools.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.item.QuarkMusicDiscItem;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "tools", hasSubscriptions = true)
public class EndermoshMusicDiscModule extends QuarkModule {

	@Config private boolean playEndermoshDuringEnderdragonFight = false;

	@Config private boolean addToEndCityLoot = true;
	@Config private int lootWeight = 5;
	@Config private int lootQuality = 1;

	@Hint public static QuarkMusicDiscItem endermosh;

	@OnlyIn(Dist.CLIENT) private boolean isFightingDragon;
	@OnlyIn(Dist.CLIENT) private int delay;
	@OnlyIn(Dist.CLIENT) private SimpleSoundInstance sound;

	@LoadEvent
	public final void register(ZRegister event) {
		endermosh = new QuarkMusicDiscItem(14, () -> QuarkSounds.MUSIC_ENDERMOSH, "endermosh", this, 3783); // Tick length calculated from endermosh.ogg - 3:09.150
	}

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		if(addToEndCityLoot) {
			ResourceLocation res = event.getName();
			if(res.equals(BuiltInLootTables.END_CITY_TREASURE)) {
				LootPoolEntryContainer entry = LootItem.lootTableItem(endermosh)
						.setWeight(lootWeight)
						.setQuality(lootQuality)
						.build();

				MiscUtil.addToLootTable(event.getTable(), entry);
			}
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void tick(ClientTickEvent event) {
		if(event.phase == Phase.END && playEndermoshDuringEnderdragonFight) {
			boolean wasFightingDragon = isFightingDragon;

			Minecraft mc = Minecraft.getInstance();
			isFightingDragon = mc.level != null
					&& mc.level.dimension().location().equals(LevelStem.END.location())
					&& mc.gui.getBossOverlay().shouldPlayMusic();

			final int targetDelay = 50;

			if(isFightingDragon) {
				if(delay == targetDelay) {
					sound = SimpleSoundInstance.forMusic(QuarkSounds.MUSIC_ENDERMOSH);
					mc.getSoundManager().playDelayed(sound, 0);
					mc.gui.setNowPlaying(endermosh.getDisplayName());
				}

				double x = mc.player.getX();
				double z = mc.player.getZ();

				if(mc.screen == null && ((x*x) + (z*z)) < 3000) // is not in screen and within island
					delay++;

			} else if(wasFightingDragon && sound != null) {
				mc.getSoundManager().stop(sound);
				delay = 0;
				sound = null;
			}
		}
	}

}
