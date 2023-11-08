package org.violetmoon.zeta.event.play.loading;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.violetmoon.zeta.capability.ZetaCapability;
import org.violetmoon.zeta.capability.ZetaCapabilityManager;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZAttachCapabilities<T> extends IZetaPlayEvent {
    ZetaCapabilityManager getCapabilityManager();
    T getObject();

    <C> void addCapability(ResourceLocation key, ZetaCapability<C> cap, C impl);

    interface ItemStackCaps extends ZAttachCapabilities<ItemStack> { }
    interface BlockEntityCaps extends ZAttachCapabilities<BlockEntity> { }
    interface LevelCaps extends ZAttachCapabilities<Level> { }

    @Deprecated //Forge only API, we should migrate off ICapabilityProvider.
    void addCapabilityForgeApi(ResourceLocation key, ICapabilityProvider cap);
}
