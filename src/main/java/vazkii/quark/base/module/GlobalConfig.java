package vazkii.quark.base.module;

import net.minecraftforge.common.config.Configuration;

public final class GlobalConfig {

	public static boolean enableAntiOverlap;
	public static boolean enableSeasonalFeatures;
	public static boolean enableConfigCommand;
	public static boolean enableVariants;

	public static void initGlobalConfig() {
		String category = "_global";
		
		ConfigHelper.needsRestart = ConfigHelper.allNeedRestart = true;
		
		enableAntiOverlap = ConfigHelper.loadPropBool("Enable Anti-Overlap", category, 
				"Set this to false to remove the system that has features turn themselves off automatically when "
				+ "other mods are present that add similar features."
				+ "\nNote that you can force features to be enabled individually through their respective configs.", true);
		
		enableSeasonalFeatures = ConfigHelper.loadPropBool("Enable Seasonal Features", category,
				"Whether features that are based on the time of year should be enabled."
				+ "\nAn example is chests turning to prsents when it's Christmas."
				+ "\nNote that this will not affect vanilla's own seasonal features.", true);
		
		enableConfigCommand = ConfigHelper.loadPropBool("Enable Quark Config Command", category,
				"Adds the /quarkconfig command which allows for modification of the Quark config file through any means that can run commands at permission 2 (command block level) or higher."
				+ "\nAn example syntax of the command would be /quarkconfig management \"store to chests\" \"B:Invert button\" true nosave playerdude"
				+ "\nDoing this would set the dropoff button for playerdude to be inverted. "
				+ "\"save\" means it should save the changes to the config file on disk. Using \"nosave\" won't save."
				+ "\nAnother example can be /quarkconfig tweaks - \"Shearable chickens\" false"
				+ "\nThis disables shearable chickens for everybody on the server. \"nosave\" doesn't need to be included, as it's the default."
				+ "\n\"nosave\" does need to be there if a player name is used. Lastly, - signifies no subcategory inside the module.", true);
		
		enableVariants = ConfigHelper.loadPropBool("Allow Block Variants", category, 
				"Set this to false to disable stairs, slabs, and walls, mod-wide. As these blocks can use a lot of Block IDs,\n"
				+ "this is helpful to reduce the load, if you intend on running a really large modpack.\n"
				+ "Note: Blocks that require stairs and/or slabs for their recipes (such as Soul Sandstone or Midori) won't be affected.", true);
		
		ConfigHelper.needsRestart = ConfigHelper.allNeedRestart = false;
	}
	
	public static void changeConfig(String moduleName, String category, String key, String value, boolean saveToFile) {
		if(!enableConfigCommand)
			return;
		
		Configuration config = ModuleLoader.config;
		String fullCategory = moduleName;
		if(!category.equals("-"))
			fullCategory += "." + category;
		
		char type = key.charAt(0);
		key = key.substring(2);
		
		if(config.hasKey(fullCategory, key)) {
			boolean changed = false;

			try {
				switch(type) {
				case 'B': 
					boolean b = Boolean.parseBoolean(value);
					config.get(fullCategory, key, false).setValue(b);
				case 'I':
					int i = Integer.parseInt(value);
					config.get(fullCategory, key, 0).setValue(i);
				case 'D':
					double d = Double.parseDouble(value);
					config.get(fullCategory, key, 0.0).setValue(d);
				case 'S':
					config.get(fullCategory, key, "").setValue(value);
				}
			} catch(IllegalArgumentException e) {}
			
			if(config.hasChanged()) {
				ModuleLoader.forEachModule(module -> module.setupConfig());
				
				if(saveToFile)
					config.save();
			}
		}
	}
	
}
