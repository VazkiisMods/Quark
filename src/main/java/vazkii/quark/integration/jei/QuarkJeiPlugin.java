package vazkii.quark.integration.jei;

import javax.annotation.Nonnull;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IGlobalGuiHandler;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.management.client.gui.GuiButtonTrash;
import vazkii.quark.management.feature.DeleteItems;

@JEIPlugin
public class QuarkJeiPlugin implements IModPlugin {
	@Override
	public void register(IModRegistry registry) {
		try {
			registry.addGlobalGuiHandlers(new GlobalTrashGuiHandler());
		} catch (RuntimeException | LinkageError ignored) {
			// only JEI 4.14.0 or higher supports addGlobalGuiHandlers
			// ignore failures here to let Quark work with older versions of JEI
		}
	}

	private static class GlobalTrashGuiHandler implements IGlobalGuiHandler {
		@Override
		@Nonnull
		public Collection<Rectangle> getGuiExtraAreas() {
			if(ModuleLoader.isFeatureEnabled(DeleteItems.class)) {
				GuiButtonTrash trash = DeleteItems.getTrash();
				if(trash != null) {
					Rectangle trashArea = new Rectangle(trash.x, trash.y, trash.width, trash.height);
					return Collections.singleton(trashArea);
				}
			}
			return Collections.emptySet();
		}
	}
}
