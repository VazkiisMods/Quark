package vazkii.quark.content.tools.module;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.item.QuarkMusicDiscItem;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.zeta.event.ZLivingDeath;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(category = "tools")
public class AmbientDiscsModule extends ZetaModule {

	@Config public static boolean dropOnSpiderKill = true;
	@Config public static double volume = 3;

	@Hint(key = "ambience_discs")
	private final List<Item> discs = new ArrayList<>();

	@LoadEvent
	public void register(ZRegister event) {
		disc(QuarkSounds.AMBIENT_DRIPS);
		disc(QuarkSounds.AMBIENT_OCEAN);
		disc(QuarkSounds.AMBIENT_RAIN);
		disc(QuarkSounds.AMBIENT_WIND);
		disc(QuarkSounds.AMBIENT_FIRE);
		disc(QuarkSounds.AMBIENT_CLOCK);
		disc(QuarkSounds.AMBIENT_CRICKETS);
		disc(QuarkSounds.AMBIENT_CHATTER);
	}

	private void disc(SoundEvent sound) {
		String name = Quark.ZETA.registry.getRegistryName(sound, Registry.SOUND_EVENT).getPath().replaceAll(".+\\.", "");
		discs.add(new QuarkMusicDiscItem(15, () -> sound, name, this, Integer.MAX_VALUE));
	}

	@PlayEvent
	public void onMobDeath(ZLivingDeath event) {
		if(dropOnSpiderKill && event.getEntity() instanceof Spider && event.getSource().getEntity() instanceof Skeleton) {
			Item item = discs.get(event.getEntity().level.random.nextInt(discs.size()));
			event.getEntity().spawnAtLocation(item, 0);
		}
	}

	public static class Client {
		public static void onJukeboxLoad(JukeboxBlockEntity tile) {
			Minecraft mc = Minecraft.getInstance();
			LevelRenderer render = mc.levelRenderer;
			BlockPos pos = tile.getBlockPos();

			SoundInstance sound = render.playingRecords.get(pos);
			SoundManager soundEngine = mc.getSoundManager();
			if(sound == null || !soundEngine.isActive(sound)) {
				if(sound != null) {
					soundEngine.play(sound);
				} else {
					ItemStack stack = tile.getRecord();
					if(stack.getItem() instanceof QuarkMusicDiscItem disc)
						disc.playAmbientSound(pos);
				}
			}
		}
	}
}
