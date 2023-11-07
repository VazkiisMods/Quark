package vazkii.zeta.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public interface ZetaCapabilityManager {
	/**
	 * Register a capability with the ZetaCapabilityManager.
	 * Left side is the ZetaCapability token, right side is the platform-specific object used to back this capability.
	 */
	ZetaCapabilityManager register(ZetaCapability<?> cap, Object backing);

	<T> boolean hasCapability(ZetaCapability<T> cap, ItemStack stack);
	<T> @Nullable T getCapability(ZetaCapability<T> cap, ItemStack stack);

	<T> boolean hasCapability(ZetaCapability<T> cap, BlockEntity be);
	<T> @Nullable T getCapability(ZetaCapability<T> cap, BlockEntity be);

	<T> boolean hasCapability(ZetaCapability<T> cap, Level level);
	<T> @Nullable T getCapability(ZetaCapability<T> cap, Level level);

	//On Forge, 'target' is AttachCapabilitiesEvent<Whatever>. Yeah this is kinda janky and stupid.
	// Not sure what this looks like on Fabric.
	// TODO: give this a rethink.
	<T> void attachCapability(Object target, ResourceLocation id, ZetaCapability<T> cap, T impl);
}
