package vazkii.zetaimplforge.capability;

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.zeta.capability.ZetaCapabilityManager;
import vazkii.zeta.capability.ZetaCapability;

public class ForgeCapabilityManager implements ZetaCapabilityManager {
	protected Map<ZetaCapability<?>, Capability<?>> toForge = new IdentityHashMap<>();

	@SuppressWarnings("unchecked")
	protected <T> Capability<T> forgify(ZetaCapability<T> zcap) {
		return (Capability<T>) toForge.get(zcap);
	}

	@Override
	public ForgeCapabilityManager register(ZetaCapability<?> cap, Object backing) {
		if(backing instanceof Capability<?> forgecap)
			toForge.put(cap, forgecap);
		else
			throw new IllegalArgumentException("Can only register Capability<?> objects");

		return this;
	}

	@Override
	public <T> boolean hasCapability(ZetaCapability<T> cap, ItemStack stack) {
		return stack.getCapability(forgify(cap)).isPresent();
	}

	@SuppressWarnings("DataFlowIssue") //passing null into nonnull
	@Override
	public <T> T getCapability(ZetaCapability<T> cap, ItemStack stack) {
		return stack.getCapability(forgify(cap)).orElse(null);
	}

	@Override
	public <T> boolean hasCapability(ZetaCapability<T> cap, BlockEntity be) {
		return be.getCapability(forgify(cap)).isPresent();
	}

	@SuppressWarnings("DataFlowIssue") //passing null into nonnull
	@Override
	public <T> @Nullable T getCapability(ZetaCapability<T> cap, BlockEntity be) {
		return be.getCapability(forgify(cap)).orElse(null);
	}

	@Override
	public <T> boolean hasCapability(ZetaCapability<T> cap, Level level) {
		return level.getCapability(forgify(cap)).isPresent();
	}

	@SuppressWarnings("DataFlowIssue") //passing null into nonnull
	@Override
	public <T> @Nullable T getCapability(ZetaCapability<T> cap, Level level) {
		return level.getCapability(forgify(cap)).orElse(null);
	}

	@Override
	public <T> void attachCapability(Object target, ResourceLocation id, ZetaCapability<T> cap, T impl) {
		((AttachCapabilitiesEvent<?>) target).addCapability(id, new ImmediateProvider<>(forgify(cap), impl));
	}

	// Capability Provider For Player With No Time For Nonsense
	protected record ImmediateProvider<C>(Capability<C> cap, LazyOptional<C> impl) implements ICapabilityProvider {
		ImmediateProvider(Capability<C> cap, C impl) {
			this(cap, LazyOptional.of(() -> impl));
		}

		@Override
		public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
			return cap == this.cap ? impl.cast() : LazyOptional.empty();
		}
	}
}
