/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/06/2016, 22:33:48 (GMT)]
 */
package vazkii.quark.decoration.feature;

import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.client.render.RenderFlatItemFrame;
import vazkii.quark.decoration.entity.EntityFlatItemFrame;

public class FlatItemFrames extends Feature {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		String flatItemFrameName = "quark:flat_item_frame";
		EntityRegistry.registerModEntity(new ResourceLocation(flatItemFrameName), EntityFlatItemFrame.class, flatItemFrameName, LibEntityIDs.FLAT_ITEM_FRAME, Quark.instance, 256, 64, false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityFlatItemFrame.class, RenderFlatItemFrame.FACTORY);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
    }

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
		ItemStack itemstack = event.getItemStack();
		EnumFacing facing = event.getFace();
		BlockPos blockpos = event.getPos().offset(facing);
		World world = event.getWorld();
		EntityPlayer player = event.getEntityPlayer();

		if(event.getSide() == Side.CLIENT)
			return;

		if(!player.canPlayerEdit(blockpos, facing, itemstack))
			return;

		if(facing.getAxis() != EnumFacing.Axis.Y)
			return;

		if(itemstack.getItem() != Items.ITEM_FRAME)
			return;

		EntityHanging entityhanging = new EntityFlatItemFrame(world, blockpos, facing);

		if(entityhanging != null && entityhanging.onValidSurface()) {
			entityhanging.playPlaceSound();
			world.spawnEntity(entityhanging);
			event.setCanceled(true);

			if (!player.capabilities.isCreativeMode)
				itemstack.shrink(1);
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
}
