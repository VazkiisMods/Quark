/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [19/03/2016, 00:59:31 (GMT)]
 */
package vazkii.quark.base.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.ModuleLoader;

public class GuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {
		// NO-OP
	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return GuiQuarkConfig.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}

	public static class GuiQuarkConfig extends GuiConfig {

		public GuiQuarkConfig(GuiScreen parentScreen) {
			super(parentScreen, getAllElements(), LibMisc.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(ModuleLoader.config.toString()));
		}

		public static List<IConfigElement> getAllElements() {
			List<IConfigElement> list = new ArrayList();

			Set<String> categories = ModuleLoader.config.getCategoryNames();
			for(String s : categories)
				if(!s.contains("."))
					list.add(new DummyConfigElement.DummyCategoryElement(s, s, new ConfigElement(ModuleLoader.config.getCategory(s)).getChildElements()));

			return list;
		}

	}

}
