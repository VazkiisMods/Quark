package vazkii.quark.management.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import vazkii.arl.interf.IDropInItem;

public abstract class AbstractDropIn implements ICapabilityProvider, IDropInItem {

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == DROP_IN_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == DROP_IN_CAPABILITY ? (T) this : null;
	}

}
