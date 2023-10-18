package vazkii.quark.base.client.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import vazkii.quark.api.config.IConfigCategory;
import vazkii.quark.api.config.IConfigElement;
import vazkii.quark.api.config.IConfigObject;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.config.external.ExternalConfigHandler;
import vazkii.quark.base.module.config.IConfigCallback;
import vazkii.zeta.module.ZetaCategory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public final class IngameConfigHandler implements IConfigCallback {

	public static final IngameConfigHandler INSTANCE = new IngameConfigHandler();

	public Map<String, TopLevelCategory> topLevelCategories = new LinkedHashMap<>();

	private IConfigCategory currCategory = null;

	private IngameConfigHandler() {}

	@Override
	public void push(String s, String comment, Object holderObject) {
		IConfigCategory newCategory;
		if(currCategory == null) {
			newCategory = new TopLevelCategory(s, comment, null);
			topLevelCategories.put(s, (TopLevelCategory) newCategory);
		} else newCategory = currCategory.addCategory(s, comment, holderObject);

		currCategory = newCategory;
	}

	@Override
	public void pop() {
		if(currCategory != null) {
			currCategory.close();
			currCategory = currCategory.getParent();
		}
	}

	@Override
	public <T> void addEntry(ConfigValue<T> value, T default_, Supplier<T> getter, String comment, Predicate<Object> restriction) {
		if(currCategory != null)
			currCategory.addEntry(value, default_, getter, comment, restriction);
	}

	public IConfigObject<Boolean> getCategoryEnabledObject(ZetaCategory category) {
		return topLevelCategories.get("categories").getModuleOption(category);
	}

	public IConfigCategory getConfigCategory(ZetaCategory category) {
		return topLevelCategories.get(category == null ? "general" : category.name);
	}

	public void refresh() {
		topLevelCategories.values().forEach(IConfigElement::refresh);
	}

	public void commit() {
		Quark.proxy.setConfigGuiSaving(true);
		try {
			commit(topLevelCategories);
			ExternalConfigHandler.instance.commit();
			Quark.proxy.handleQuarkConfigChange();
		} finally {
			Quark.proxy.setConfigGuiSaving(false);
		}
	}

	public static <T extends IConfigCategory> void commit(Map<String, T> map) {
		for(IConfigCategory c : map.values()) {
			if(c.isDirty()) {
				c.save();
				c.clean();
			}
		}
	}

}
