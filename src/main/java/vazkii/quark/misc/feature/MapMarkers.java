package vazkii.quark.misc.feature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration.Type;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.recipe.MapPinningRecipe;

public class MapMarkers extends Feature {

	public static final String TAG_ADD_PIN = "quark:needs_pin";
	
	boolean useRightClick;
	
	@Override
	public void setupConfig() {
		useRightClick = loadPropBool("Use Right Click", "If enabled, replaces the recipe to add the marker with right clicking for the same purpose", false);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		if(!useRightClick)
			new MapPinningRecipe();
	}
	
	@SubscribeEvent
	public void rightClick(RightClickItem event) {
		if(useRightClick)
			addMarker(event.getItemStack(), event.getEntityPlayer());
	}
	
	@SubscribeEvent
	public void tick(PlayerTickEvent event) {
		if(!useRightClick && event.phase == Phase.END) {
			EntityPlayer player = event.player;
			for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(stack.hasTagCompound() && ItemNBTHelper.getBoolean(stack, TAG_ADD_PIN, false)) {
					addMarker(stack, player);
					ItemNBTHelper.setBoolean(stack, TAG_ADD_PIN, false);
				}
			}
		}
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	public static void addMarker(ItemStack stack, EntityPlayer player) {
		if(stack.getItem() == Items.FILLED_MAP) {
			MapData data = ((ItemMap) Items.FILLED_MAP).getMapData(stack, player.world);
			
			if(data != null)
				data.addTargetDecoration(stack, player.getPosition(), "Pin" + player.getPosition().hashCode(), Type.TARGET_X);
		}
	}
	
}
