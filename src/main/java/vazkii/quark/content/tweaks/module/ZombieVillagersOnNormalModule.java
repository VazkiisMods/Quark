package vazkii.quark.content.tweaks.module;

import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = "tweaks", hasSubscriptions = true)
public class ZombieVillagersOnNormalModule extends QuarkModule {

	@SubscribeEvent
	public void onConversion(LivingConversionEvent.Pre event) {
		if(event.getEntity().getType() == EntityType.VILLAGER && event.getOutcome() == EntityType.ZOMBIE_VILLAGER) {
			Villager villager = (Villager) event.getEntity();
			Level level = villager.level;

			if(level instanceof ServerLevelAccessor serverLevel) {
				ZombieVillager zombievillager = villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
				zombievillager.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), null);
				zombievillager.setVillagerData(villager.getVillagerData());
				zombievillager.setGossips(villager.getGossips().store(NbtOps.INSTANCE).getValue());
				zombievillager.setTradeOffers(villager.getOffers().createTag());
				zombievillager.setVillagerXp(villager.getVillagerXp());

				ForgeEventFactory.onLivingConvert(villager, zombievillager);
				level.levelEvent(null, LevelEvent.SOUND_ZOMBIE_INFECTED, villager.blockPosition(), 0);

				event.setCanceled(true);
			}
		}
	}

}
