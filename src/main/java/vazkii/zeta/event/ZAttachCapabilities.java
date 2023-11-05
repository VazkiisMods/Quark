package vazkii.zeta.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZAttachCapabilities<T> extends IZetaPlayEvent {
    T getObject();
    void addCapability(ResourceLocation key, ICapabilityProvider cap);
}
