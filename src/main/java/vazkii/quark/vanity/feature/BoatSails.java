/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/06/2016, 17:22:47 (GMT)]
 */
package vazkii.quark.vanity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class BoatSails extends Feature {

	private static DataParameter<ItemStack> bannerData;
	private static final String TAG_BANNER = "quark:banner";

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		bannerData = EntityDataManager.<ItemStack>createKey(EntityBoat.class, DataSerializers.ITEM_STACK);
	}
	
	@SubscribeEvent
	public void onEntityInit(EntityConstructing event) {
		if(event.getEntity() instanceof EntityBoat) {
			EntityDataManager manager = event.getEntity().getDataManager();
			manager.register(bannerData, ItemStack.EMPTY);
		}
	}

	@SubscribeEvent
	public void onEntityInteract(EntityInteract event) {
		Entity target = event.getTarget();
		EntityPlayer player = event.getEntityPlayer();

		if(target instanceof EntityBoat && !target.getPassengers().contains(player)) {
			ItemStack banner = getBanner((EntityBoat) target);
			if(!banner.isEmpty())
				return;

			EnumHand hand = EnumHand.MAIN_HAND;
			ItemStack stack = player.getHeldItemMainhand();
			if(stack.isEmpty() || !(stack.getItem() instanceof ItemBanner)) {
				stack = player.getHeldItemOffhand();
				hand = EnumHand.OFF_HAND;
			}

			if(!stack.isEmpty() && stack.getItem() instanceof ItemBanner) {
				ItemStack copyStack = stack.copy();
				player.swingArm(hand);
				target.getDataManager().set(bannerData, copyStack);

				NBTTagCompound cmp = new NBTTagCompound();
				copyStack.writeToNBT(cmp);
				target.getEntityData().setTag(TAG_BANNER, cmp);

				if(!event.getWorld().isRemote) {
					event.setCanceled(true);
					if(!player.capabilities.isCreativeMode) {
						stack.shrink(1);

						if(stack.getCount() <= 0)
							player.setHeldItem(hand, ItemStack.EMPTY);
					}
				}
			}
		}
	}

	public static ItemStack getBanner(EntityBoat boat) {
		return boat.getDataManager().get(bannerData);
	}
	
	public static void onBoatUpdate(EntityBoat boat) {
		if(boat.getEntityWorld().isRemote || !ModuleLoader.isFeatureEnabled(BoatSails.class))
			return;
		
		ItemStack dataStack = boat.getDataManager().get(bannerData);

		NBTTagCompound cmp = boat.getEntityData().getCompoundTag(TAG_BANNER);
		ItemStack nbtStack = new ItemStack(cmp);

		if(dataStack != nbtStack)
			boat.getDataManager().set(bannerData, nbtStack);
	}

	public static void dropBoatBanner(EntityBoat boat) {
		if(!ModuleLoader.isFeatureEnabled(BoatSails.class))
			return;

		ItemStack banner = getBanner(boat);
		if(!banner.isEmpty()) {
			banner.setCount(1);
			boat.entityDropItem(banner, 0F);
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
