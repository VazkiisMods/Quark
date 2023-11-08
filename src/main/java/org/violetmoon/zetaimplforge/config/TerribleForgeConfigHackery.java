package org.violetmoon.zetaimplforge.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.io.Serial;
import java.lang.reflect.Method;
import java.nio.file.Path;

public class TerribleForgeConfigHackery {
	private static final Method SET_CONFIG_DATA = ObfuscationReflectionHelper.findMethod(ModConfig.class, "setConfigData", CommentedConfig.class);
	private static final Method SETUP_CONFIG_FILE = ObfuscationReflectionHelper.findMethod(ConfigFileTypeHandler.class,
		"setupConfigFile", ModConfig.class, Path.class, ConfigFormat.class);

	public static void registerAndLoadConfigEarlierThanUsual(ForgeConfigSpec spec) {
		ModContainer container = ModLoadingContext.get().getActiveContainer();
		ModConfig modConfig = new ModConfig(ModConfig.Type.COMMON, spec, container);
		container.addConfig(modConfig);

		//same stuff that forge config tracker does
		ConfigFileTypeHandler handler = modConfig.getHandler();
		//read config without setting file watcher which could cause resets. forge will load it later
		CommentedFileConfig configData = readConfig(handler, FMLPaths.CONFIGDIR.get(), modConfig);
		//CommentedFileConfig configData = handler.reader(FMLPaths.CONFIGDIR.get()).apply( modConfig);

		SET_CONFIG_DATA.setAccessible(true);
		try {
			SET_CONFIG_DATA.invoke(modConfig, configData);
		}catch (Exception ignored){}
		//container.dispatchConfigEvent(IConfigEvent.loading(this.config));

		modConfig.save();
	}

	//we need this so we dont add a second file watcher. Same as handler::reader
	private static CommentedFileConfig readConfig(ConfigFileTypeHandler handler, Path configBasePath, ModConfig c) {
		Path configPath = configBasePath.resolve(c.getFileName());
		CommentedFileConfig configData = CommentedFileConfig.builder(configPath).sync().
			preserveInsertionOrder().
			autosave().
			onFileNotFound((newfile, configFormat)->{
				try {
					return (Boolean) SETUP_CONFIG_FILE.invoke(handler, c, newfile, configFormat);
				} catch (Exception e) {
					throw new ConfigLoadingException(c, e);
				}
			}).
			writingMode(WritingMode.REPLACE).
			build();
		try {
			configData.load();
		}
		catch (Exception ex) {
			throw new ConfigLoadingException(c, ex);
		}
		return configData;
	}

	private static class ConfigLoadingException extends RuntimeException {
		@Serial
		private static final long serialVersionUID = 1554369973578001612L;

		public ConfigLoadingException(ModConfig config, Exception cause) {
			super("Failed loading config file " + config.getFileName() + " of type " + config.getType() + " for modid " + config.getModId(), cause);
		}
	}
}
