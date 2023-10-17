package vazkii.zetaimplforge.registry;

import java.util.Collection;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import vazkii.zeta.registry.ZetaRegistry;
import vazkii.zetaimplforge.ForgeZeta;

public class ForgeZetaRegistry extends ZetaRegistry {
	public ForgeZetaRegistry(ForgeZeta z, String modid) {
		super(z, modid);

		FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterEvent e) ->
			register(e.getRegistryKey(), e.getForgeRegistry()));
	}

	@SuppressWarnings({ "unchecked" })
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
				ResourceLocation name = getInternalName(entry);
				z.log.debug("Registering to " + registryRes + " - " + name);
				registry.register(name, (T) entry);
			}

			clearDeferCache(registryRes);
		}
	}
}
