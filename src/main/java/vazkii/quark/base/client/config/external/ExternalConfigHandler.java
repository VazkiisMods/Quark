package vazkii.quark.base.client.config.external;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Consumer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import vazkii.quark.api.config.IExternalCategory;
import vazkii.quark.api.config.IQuarkConfig;
import vazkii.quark.base.Quark;
import vazkii.quark.base.QuarkClient;
import vazkii.quark.base.client.config.ConfigCategory;

public final class ExternalConfigHandler implements IQuarkConfig {
	
	public static ExternalConfigHandler instance;
	
	public Multimap<String, IExternalCategory> externalCategories = HashMultimap.create();
	public ConfigCategory mockCategory = null;
	private int lastConfigChange = 0;
	
	public void setAPIHandler() {
		instance = this;
		Holder.instance = this;
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void configChanged(ModConfigEvent event) {
		String modid = event.getConfig().getModId();
		if(externalCategories.containsKey(modid) && Quark.ZETA.ticker_SHOULD_NOT_BE_HERE.ticksInGame - lastConfigChange > 10) {
			lastConfigChange = Quark.ZETA.ticker_SHOULD_NOT_BE_HERE.ticksInGame;
			for(IExternalCategory category : externalCategories.get(modid))
				category.refresh();
		}
	}
	
	public boolean hasAny() {
		return !externalCategories.isEmpty();
	}
	
	public void commit() {
		externalCategories.values().stream().forEach(IExternalCategory::commit);
	}
	
	@Override
	public IExternalCategory registerExternalCategory(String modid, String name, Consumer<IExternalCategory> onChangedCallback) {
		if(mockCategory == null)
			mockCategory = new ConfigCategory("friends", "", null, null);
		
		ExternalCategory category = new ExternalCategory(name, onChangedCallback, mockCategory);
		externalCategories.put(modid, category);
		mockCategory.addCategory(category);
		
		return category;
	}

	@Override
	public Consumer<IExternalCategory> writeToFileCallback(File file) {
		return c -> {
			if(c.isDirty()) {
				try {
					file.createNewFile();
					PrintStream stream = new PrintStream(file);
					c.print("", stream);
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				c.clean();
			}
		};
	}
	
}
