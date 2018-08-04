/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 21:51:45 (GMT)]
 */
package vazkii.quark.base.proxy;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import vazkii.quark.base.client.DevCapeHandler;
import vazkii.quark.base.client.ResourceProxy;
import vazkii.quark.base.client.gui.ConfigEvents;
import vazkii.quark.base.lib.LibObfuscation;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.vanity.client.emotes.EmoteHandler;
import vazkii.quark.vanity.feature.EmoteSystem;

public class ClientProxy extends CommonProxy {

	static ResourceProxy resourceProxy;
	
	static {
		List<IResourcePack> packs = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), LibObfuscation.DEFAULT_RESOURCE_PACKS);
		resourceProxy = new ResourceProxy();
		packs.add(resourceProxy);
		
		EmoteSystem.addResourcePack(packs);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		ModuleLoader.preInitClient(event);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		ModuleLoader.initClient(event);

		MinecraftForge.EVENT_BUS.register(DevCapeHandler.class);
		MinecraftForge.EVENT_BUS.register(ConfigEvents.class);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		ModuleLoader.postInitClient(event);
	}

	@Override
	public void doEmote(String playerName, String emoteName) {
		World world = Minecraft.getMinecraft().world;
		EntityPlayer player = world.getPlayerEntityByName(playerName);
		if(player != null && player instanceof AbstractClientPlayer)
			EmoteHandler.putEmote((AbstractClientPlayer) player, emoteName);
	}

	@Override
	public void addResourceOverride(String space, String dir, String file, String ext) {
		resourceProxy.addResource(space, dir, file, ext);
	}

}
