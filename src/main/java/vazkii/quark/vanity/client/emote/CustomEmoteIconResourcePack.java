package vazkii.quark.vanity.client.emote;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.Quark;
import vazkii.quark.vanity.module.EmotesModule;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.*;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class CustomEmoteIconResourcePack extends ResourcePack {

	private final List<String> verifiedNames = new ArrayList<>();
	private final List<String> existingNames = new ArrayList<>();

	public CustomEmoteIconResourcePack() {
		super(EmotesModule.emotesDir);
	}

	@Nonnull
	@Override
	public Set<String> getResourceNamespaces(@Nonnull ResourcePackType type) {
		if (type == ResourcePackType.CLIENT_RESOURCES)
			return ImmutableSet.of(EmoteHandler.CUSTOM_EMOTE_NAMESPACE);
		return ImmutableSet.of();
	}

	@Nonnull
	@Override
	protected InputStream getInputStream(@Nonnull String name) throws IOException {
		if(name.equals("pack.mcmeta"))
			return Quark.class.getResourceAsStream("/proxypack.mcmeta");
		
		if(name.equals("pack.png"))
			return Quark.class.getResourceAsStream("/proxypack.png");
		
		File file = getFile(name);
		if(!file.exists())
			throw new FileNotFoundException(name);
		
		return new FileInputStream(file);
	}
	
	@Nonnull
	@Override
	public Collection<ResourceLocation> getAllResourceLocations(@Nonnull ResourcePackType type, @Nonnull String pathIn, String idk, int maxDepth, @Nonnull Predicate<String> filter) {
		File rootPath = new File(this.file, type.getDirectoryName());
		List<ResourceLocation> allResources = Lists.newArrayList();

		for (String namespace : this.getResourceNamespaces(type))
			this.crawl(new File(new File(rootPath, namespace), pathIn), maxDepth, namespace, allResources, pathIn + "/", filter);

		return allResources;
	}

	private void crawl(File rootPath, int maxDepth, String namespace, List<ResourceLocation> allResources, String path, Predicate<String> filter) {
		File[] files = rootPath.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					if (maxDepth > 0)
						this.crawl(file, maxDepth - 1, namespace, allResources, path + file.getName() + "/", filter);
				} else if (!file.getName().endsWith(".mcmeta") && filter.test(file.getName())) {
					try {
						allResources.add(new ResourceLocation(namespace, path + file.getName()));
					} catch (ResourceLocationException e) {
						Quark.LOG.error(e.getMessage());
					}
				}
			}
		}
	}

	@Override
	public void close() {
		// NO-OP
	}

	@Override
	protected boolean resourceExists(@Nonnull String name) {
		if(!verifiedNames.contains(name)) {
			File file = getFile(name);
			if(file.exists())
				existingNames.add(name);
			verifiedNames.add(name);
		}
		
		return existingNames.contains(name);
	}
	
	private File getFile(String name) {
		String filename = name.substring(name.indexOf(":") + 1) + ".png";
		return new File(EmotesModule.emotesDir, filename);
	}

	@Override
	public boolean isHidden() {
		return true;
	}

	@Nonnull
	@Override
	public String getName() {
		return "quark-emote-pack";
	}


}
