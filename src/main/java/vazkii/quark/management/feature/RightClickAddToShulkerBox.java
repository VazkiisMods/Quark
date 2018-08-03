package vazkii.quark.management.feature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.management.capability.ShulkerBoxDropIn;

public class RightClickAddToShulkerBox extends Feature {

	private static final ResourceLocation SHULKER_BOX_CAP = new ResourceLocation(LibMisc.MOD_ID, "shulker_box_drop_in");
	
	@SubscribeEvent
	public void onAttachCapability(AttachCapabilitiesEvent<ItemStack> event) {
		if(event.getObject().getItem() instanceof ItemShulkerBox)
			event.addCapability(SHULKER_BOX_CAP, new ShulkerBoxDropIn());
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
