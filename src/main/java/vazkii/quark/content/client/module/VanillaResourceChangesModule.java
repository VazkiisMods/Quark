package vazkii.quark.content.client.module;
//package vazkii.quark.client.module;
//
//import java.io.InputStreamReader;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import com.google.gson.Gson;
//
//import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
//import net.minecraftforge.common.ForgeConfigSpec.Builder;
//import vazkii.quark.base.Quark;
//import vazkii.quark.base.module.LoadModule;
//import vazkii.quark.base.module.Module;
//import vazkii.quark.base.module.ModuleCategory;
//
//@LoadModule(category = "client")
//public class VanillaResourceChangesModule extends Module {
//
//	private static final Pattern FILE_PATTERN = Pattern.compile("([^\\/]+)\\/(.+)\\/(.+)");
//	private static final String OVERRIDES_JSON_FILE = "/assets/" + Quark.MOD_ID + "/overrides.json";
//	private static final Gson GSON = new Gson();
//	
//	public static OverrideHolder overrideHolder = null;
//	
//	@Override
//	public void buildConfigSpec(Builder builder, List<Runnable> callbacks) {
//		if(overrideHolder == null) {
//			InputStreamReader reader = new InputStreamReader(Quark.class.getResourceAsStream(OVERRIDES_JSON_FILE));
//			overrideHolder = GSON.fromJson(reader, OverrideHolder.class);
//		}
//		
//		for(OverrideEntry entry : overrideHolder.overrides) {
//			BooleanValue val = builder.define("Enable " + entry.name, entry.enabledByDefault);
//			callbacks.add(() -> entry.configStatus = enabled && val.get());
//			
//			for(String file : entry.files) {
//				Matcher m = FILE_PATTERN.matcher(file);
//				if(!m.matches())
//					throw new IllegalArgumentException("Invalid override file: " + file);
//				
//				Quark.proxy.addResourceOverride(m.group(1), m.group(2), m.group(3), () -> entry.configStatus);
//			}
//		}
//	}
//	
//	private static class OverrideHolder {
//		
//		public List<OverrideEntry> overrides;
//		
//	}
//	
//	private static class OverrideEntry {
//		
//		public String name;
//		public boolean enabledByDefault;
//		public List<String> files;
//		
//		private boolean configStatus = false;
//		
//	}
//	
//}
