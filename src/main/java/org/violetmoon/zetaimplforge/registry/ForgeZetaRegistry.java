package org.violetmoon.zetaimplforge.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import org.violetmoon.zeta.registry.ZetaRegistry;
import org.violetmoon.zetaimplforge.ForgeZeta;

import java.util.Collection;
import java.util.function.Supplier;

public class ForgeZetaRegistry extends ZetaRegistry {
	public ForgeZetaRegistry(ForgeZeta z) {
		super(z);

		FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterEvent e) ->
			register(e.getRegistryKey(), e.getForgeRegistry()));
	}

	@SuppressWarnings("unchecked")
	private <T> void register(ResourceKey<? extends Registry<?>> key, IForgeRegistry<T> registry) {
		ResourceLocation registryRes = key.location();

		Collection<Supplier<Object>> ourEntries = getDefers(registryRes);
		if(ourEntries != null && !ourEntries.isEmpty()) {
			if(registry == null) {
				z.log.error(registryRes + " does not have a forge registry");
				return;
			}

			for(Supplier<Object> supplier : ourEntries) {
				Object entry = supplier.get();
				ResourceLocation name = internalNames.get(entry);
				z.log.debug("Registering to " + registryRes + " - " + name);
				registry.register(name, (T) entry);
			}

			clearDeferCache(registryRes);
		}
	}
}
