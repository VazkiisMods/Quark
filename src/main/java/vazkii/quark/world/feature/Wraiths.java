/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [03/07/2016, 03:48:53 (GMT)]
 */
package vazkii.quark.world.feature;

import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.potion.PotionMod;
import vazkii.quark.world.client.render.RenderWraith;
import vazkii.quark.world.entity.EntityWraith;
import vazkii.quark.world.item.ItemSoulBead;

public class Wraiths extends Feature {

	public static Item soul_bead;

	public static Potion curse;

	public static int curseTime;
	public static boolean enableCurse;

	int weight, min, max;
	int curseRange;

	@Override
	public void setupConfig() {
		weight = loadPropInt("Spawn Weight", "The higher, the more will spawn", 60);
		min = loadPropInt("Smallest spawn group", "", 4);
		max = loadPropInt("Largest spawn group", "", 6);
		curseRange = loadPropInt("Curse Range", "", 64);
		curseTime = loadPropInt("Curse Time", "How long the curse effect lasts for (in ticks)", 24000);
		enableCurse = loadPropBool("Enable Curse", "", true);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		soul_bead = new ItemSoulBead();

		if(enableCurse)
			curse = new PotionMod("curse", true, 0x000000, 0);

		String wraithName = "quark:wraith";
		EntityRegistry.registerModEntity(new ResourceLocation(wraithName), EntityWraith.class, wraithName, LibEntityIDs.WRAITH, Quark.instance, 80, 3, true, 0xececec, 0xbdbdbd);
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		EntityRegistry.addSpawn(EntityWraith.class, weight, min, max, EnumCreatureType.MONSTER, BiomeDictionary.getBiomes(Type.NETHER).toArray(new Biome[0]));
	}

	@Override
	public void preInitClient(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityWraith.class, RenderWraith.FACTORY);
	}

	@SubscribeEvent
	public void onSpawn(LivingSpawnEvent.CheckSpawn event) {
		if(event.getResult() != Result.ALLOW && event.getEntityLiving() instanceof IMob && event.getWorld() instanceof WorldServer) {
			List<EntityPlayer> players = ((WorldServer) event.getWorld()).playerEntities;
			for(EntityPlayer player : players)
				if(player.getActivePotionEffect(curse) != null && player.getDistanceSq(event.getEntity()) < curseRange * curseRange) {
					if(!(event.getEntity() instanceof EntityCreeper))
						event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
					event.setResult(Result.ALLOW);
					return;
				}
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
