package org.violetmoon.zeta.event.play.loading;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.config.ConfigFlagManager;
import org.violetmoon.quark.base.handler.MiscUtil;
import org.violetmoon.zeta.config.ConfigObjectMapper;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public interface ZGatherHints extends IZetaPlayEvent, BiConsumer<Item, Component> {
	default void hintItem(ItemLike itemLike, Object... extra) {
		Item item = itemLike.asItem();
		ResourceLocation res = Registry.ITEM.getKey(item);
		String ns = res.getNamespace();
		String path = res.getPath();

		//TODO ZETA: quark hardcoding
		if(ns.equals(Quark.MOD_ID))
			ns = "";
		else ns += ".";

		hintItem(itemLike, ns + path, extra);
	}

	default void hintItem(ItemLike itemLike, String key, Object... extra) {
		Item item = itemLike.asItem();
		String hint = "quark.jei.hint." + key; //TODO ZETA: quark hardcoding
		accept(item, Component.translatable(hint, extra));
	}

	default void gatherHintsFromModule(ZetaModule module, ConfigFlagManager cfm) {
		if(!module.enabled)
			return;

		List<Field> fields = ConfigObjectMapper.walkModuleFields(module.getClass());

		Map<String, Field> fieldsByName = new HashMap<>();
		for(Field f : fields)
			fieldsByName.put(f.getName(), f);

		for(Field f : fields) {
			try {
				Hint hint = f.getAnnotation(Hint.class);
				if(hint == null)
					continue;
				f.setAccessible(true);

				//Target
				Object target = ConfigObjectMapper.getField(module, f);
				if(target == null)
					continue;

				//Flag
				String flag = hint.value();
				if(!flag.isEmpty() && cfm.getFlag(flag) == !hint.negate())
					continue;

				//Translation, & gathering extra content
				String key = hint.key();
				List<Object> extraList = new ArrayList<>(hint.content().length);
				for(String c : hint.content()) {
					if(c.isEmpty())
						continue;

					Field extraField = fieldsByName.get(c);
					if(extraField == null)
						throw new RuntimeException("No field " + c + " referenced in @Hint " + f);

					Object yes = ConfigObjectMapper.getField(module, extraField);
					extraList.add(yes);
				}
				Object[] extra = extraList.toArray(new Object[0]);

				//Application
				if(target instanceof TagKey<?> tkey)
					applyTag(tkey, key, extra);
				else if(target instanceof Iterable<?> iter)
					applyIterable(iter, key, extra);
				else
					applyObject(target, key, extra);

			} catch (Exception e) {
				throw new RuntimeException("Problem applying annotation hint " + f.getName() +
					" from module " + module.getClass().getName() +
					": " + e.getMessage(), e);
			}
		}
	}

	private void applyTag(TagKey<?> tkey, String key, Object... extra) {
		if(key.isEmpty())
			key = tkey.location().getPath();

		try {
			List<?> tagItems = MiscUtil.getTagValues(BuiltinRegistries.ACCESS, tkey);
			applyIterable(tagItems, key, extra);
		} catch(IllegalStateException e) {
			throw new RuntimeException("TagKey " + tkey + " failed to load.", e);
		}
	}

	private void applyIterable(Iterable<?> iter, String key, Object... extra) {
		if(key.isEmpty())
			throw new RuntimeException("Multi-item @Hints need a defined key.");

		for(Object obj : iter)
			applyObject(obj, key, extra);
	}

	private void applyObject(Object obj, String key, Object... extra) {
		if(obj instanceof ItemLike ilike)
			applyItemLike(ilike, key, extra);
		else
			throw new RuntimeException("Not an ItemLike.");
	}

	private void applyItemLike(ItemLike itemLike, String key, Object... extra) {
		if(key.isEmpty())
			hintItem(itemLike, extra);
		else
			hintItem(itemLike, key, extra);
	}
}
