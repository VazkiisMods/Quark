package vazkii.quark.base.module.config;

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
import org.apache.commons.lang3.text.WordUtils;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.GeneralConfig;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.module.ZetaCategory;
import vazkii.zeta.module.ZetaModule;

import java.io.Serial;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConfigResolver {

    private static final Method SET_CONFIG_DATA = ObfuscationReflectionHelper.findMethod(ModConfig.class, "setConfigData", CommentedConfig.class);
    private static final Method SETUP_CONFIG_FILE = ObfuscationReflectionHelper.findMethod(ConfigFileTypeHandler.class,
            "setupConfigFile", ModConfig.class, Path.class, ConfigFormat.class);

    //TODO ZETA: made this public (sorry) to simplify hints
    public final ConfigFlagManager flagManager;

    private final List<Runnable> refreshRunnables = new LinkedList<>();
    private ModConfig config;

    public ConfigResolver() {
        this.flagManager = new ConfigFlagManager();
    }

    public ModConfig getConfig() {
        return config;
    }

    public void registerConfigBoundElements() {
        flagManager.registerConfigBoundElements();
    }

    public void makeSpec() {
        ForgeConfigSpec.Builder forgeBuilder = new ForgeConfigSpec.Builder();
        IConfigCallback callback = Quark.proxy.getConfigCallback();
        IConfigBuilder builder = new QuarkConfigBuilder(forgeBuilder, callback);

        ForgeConfigSpec spec = builder.configure(this::build);

        ModContainer container = ModLoadingContext.get().getActiveContainer();
        this.config = new ModConfig(ModConfig.Type.COMMON, spec, container);
        container.addConfig(config);
        //load early for creative tabs
		loadFromFile(config, container);
    }

    private void loadFromFile(ModConfig modConfig, ModContainer container) {
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

        //Note that here normal config reload stuff isnt called as quark is set to only run those after registration
        //will run later but this means we cant access some config values that depend on the reload for tabs
        configChanged();
        modConfig.save();
    }

    //we need this so we dont add a second file watcher. Same as handler::reader
    private CommentedFileConfig readConfig(ConfigFileTypeHandler handler, Path configBasePath, ModConfig c) {
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

    public void configChanged() {
        flagManager.clear();
        refreshRunnables.forEach(Runnable::run);
    }

    private Void build(IConfigBuilder builder) {
        builder.push("general", null);
        try {
            ConfigObjectSerializer.serialize(builder, flagManager, refreshRunnables, GeneralConfig.INSTANCE);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create quark general config", e);
        }
        builder.pop();

        builder.push("categories", null);
        buildCategoryList(builder);
        builder.pop();

        for (ZetaCategory category : Quark.ZETA.modules.getCategories())
            buildCategory(builder, category);

        return null;
    }

    private void buildCategoryList(IConfigBuilder builder) {
        for(ZetaCategory category : Quark.ZETA.modules.getCategories()) {
            ForgeConfigSpec.ConfigValue<Boolean> value = builder.defineBool(
              WordUtils.capitalizeFully(category.name),
              () -> Quark.ZETA.modules.MOVE_TO_CONFIG_categoryIsEnabled(category),
              true
            );
            refreshRunnables.add(() -> Quark.ZETA.modules.MOVE_TO_CONFIG_setCategoryEnabled(category, value.get()));
        }
    }

    private void buildCategory(IConfigBuilder builder, ZetaCategory category) {
        builder.push(category.name, category);

        List<ZetaModule> modules = Quark.ZETA.modules.modulesInCategory(category);
        Map<ZetaModule, Runnable> setEnabledRunnables = new HashMap<>();

        for (ZetaModule module : modules) {
            if (!module.description.isEmpty())
                builder.comment(module.description);

            ForgeConfigSpec.ConfigValue<Boolean> value = builder.defineBool(module.displayName, () -> module.configEnabled, module.enabledByDefault);

            setEnabledRunnables.put(module, () -> {
                module.setEnabled(Quark.ZETA, value.get() && Quark.ZETA.modules.MOVE_TO_CONFIG_categoryIsEnabled(category));

                //TODO: zetamodule flags and stuff
                flagManager.putEnabledFlag(module);
            });
        }

        for (ZetaModule module : modules)
            buildModule(builder, module, setEnabledRunnables.get(module));

        builder.pop();
    }

    private void buildModule(IConfigBuilder builder, ZetaModule module, Runnable setEnabled) {
        builder.push(module.lowercaseName, module);

        if (module.antiOverlap != null && module.antiOverlap.size() > 0)
            addModuleAntiOverlap(builder, module);

        refreshRunnables.add(setEnabled);

        try {
            ConfigObjectSerializer.serialize(builder, flagManager, refreshRunnables, module);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create config spec for module " + module.displayName, e);
        }

        refreshRunnables.add(() -> {
            //TODO ZETA: figure out how flags interact with modules
            if(module instanceof QuarkModule qm)
                qm.pushFlags(flagManager);
        });

        builder.pop();
    }

    private void addModuleAntiOverlap(IConfigBuilder builder, ZetaModule module) {
        StringBuilder desc = new StringBuilder("This feature disables itself if any of the following mods are loaded: \n");
        for (String s : module.antiOverlap)
            desc.append(" - ").append(s).append("\n");
        desc.append("This is done to prevent content overlap.\nYou can turn this on to force the feature to be loaded even if the above mods are also loaded.");
        String descStr = desc.toString();

        builder.comment(descStr);
        ForgeConfigSpec.ConfigValue<Boolean> value = builder.defineBool("Ignore Anti Overlap", () -> module.ignoreAntiOverlap, false);
        refreshRunnables.add(() -> module.ignoreAntiOverlap = !GeneralConfig.useAntiOverlap || value.get());
    }

}
