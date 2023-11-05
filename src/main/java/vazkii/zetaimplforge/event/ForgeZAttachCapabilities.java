package vazkii.zetaimplforge.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import vazkii.zeta.event.ZAttachCapabilities;

public class ForgeZAttachCapabilities<T> implements ZAttachCapabilities<T> {
    private final AttachCapabilitiesEvent<T> e;

    public ForgeZAttachCapabilities(AttachCapabilitiesEvent<T> e) {
        this.e = e;
    }

    @Override
    public T getObject() {
        return e.getObject();
    }

    @Override
    public void addCapability(ResourceLocation key, ICapabilityProvider cap) {
        e.addCapability(key, cap);
    }
}
