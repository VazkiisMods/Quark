package vazkii.quark.management.feature;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.network.message.MessageRequestPassengerChest;
import vazkii.quark.decoration.item.ItemChestBlock;
import vazkii.quark.management.client.render.RenderChestPassenger;
import vazkii.quark.management.entity.EntityChestPassenger;

public class ChestsInBoats extends Feature {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		String name = LibMisc.PREFIX_MOD + "chest_passenger";
		EntityRegistry.registerModEntity(new ResourceLocation(name), EntityChestPassenger.class, name, LibEntityIDs.CHEST_PASSENGER, Quark.instance, 64, Integer.MAX_VALUE, false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityChestPassenger.class, RenderChestPassenger.factory());
	}

	@SubscribeEvent
	public void onEntityInteract(EntityInteract event) {
		Entity target = event.getTarget();
		EntityPlayer player = event.getEntityPlayer();

		if(target instanceof EntityBoat && target.getPassengers().isEmpty()) {
			EnumHand hand = EnumHand.MAIN_HAND;
			ItemStack stack = player.getHeldItemMainhand();
			if(!isChest(stack)) {
				stack = player.getHeldItemOffhand();
				hand = EnumHand.OFF_HAND;
			}

			if(isChest(stack)) {
				World world = event.getWorld();
				EntityChestPassenger passenger = new EntityChestPassenger(world, stack);
				passenger.setPosition(target.posX, target.posY, target.posZ);
				passenger.rotationYaw = target.rotationYaw;
				
				if(!player.isCreative())
					stack.shrink(1);
				
				world.spawnEntity(passenger);
				passenger.startRiding(target);
				
				player.swingArm(hand);
				if(!event.getWorld().isRemote)
					event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onOpenGUI(GuiOpenEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player != null && event.getGui() instanceof GuiInventory && player.isRiding()) {
			Entity riding = player.getRidingEntity();
			if(riding instanceof EntityBoat) {
				List<Entity> passengers = riding.getPassengers();
				for(Entity passenger : passengers)
					if(passenger instanceof EntityChestPassenger) {
						NetworkHandler.INSTANCE.sendToServer(new MessageRequestPassengerChest());
						event.setCanceled(true);
						return;
					}
			}
		}
	}
	
	private boolean isChest(ItemStack stack) {
		return stack.getItem() == Item.getItemFromBlock(Blocks.CHEST) || stack.getItem() instanceof ItemChestBlock;
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
