package vazkii.quark.base.module.hint;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.config.ConfigFlagManager;
import vazkii.zeta.module.ZetaModule;

public class HintObject {

	private final ConfigFlagManager flagManager;
	private final ZetaModule module;
	private final String flag;
	private boolean negateFlag;
	private List<Supplier<Object>> extraContent;
	private final Supplier<Optional<Object>> fieldGetter;
	
	private String key;

	public HintObject(ConfigFlagManager flagManager, ZetaModule module, Hint hint,
					  List<Supplier<Object>> extraContent,
					  Supplier<Optional<Object>> fieldGetter) {
		this.flagManager = flagManager;
		this.module = module;
		this.flag = hint.value();
		this.negateFlag = hint.negate();
		this.fieldGetter = fieldGetter;
		this.extraContent = extraContent;

		this.key = hint.key();
	}

	public boolean isEnabled() {
		return module.enabled && (flag.isEmpty() || flagManager.getFlag(flag) != negateFlag);
	}

	public void apply(BiConsumer<Item, Component> consumer) {
		Optional<Object> optional = fieldGetter.get();
		if(optional.isPresent()) {
			Object obj = optional.get();
			
			if(obj instanceof Iterable<?> iterable)
				applyIterable(consumer, iterable);
			else if(obj instanceof TagKey<?> tagKey)
				applyTag(consumer, tagKey);
			else 
				applyObject(consumer, obj);
		}
	}

	
	private void applyTag(BiConsumer<Item, Component> consumer, TagKey<?> tagKey) {
		if(key.isEmpty())
			key = tagKey.location().getPath();
		
		try {
			List<?> tagItems = MiscUtil.getTagValues(BuiltinRegistries.ACCESS, tagKey);
			applyIterable(consumer, tagItems);
		} catch(IllegalStateException e) {
			throw new RuntimeException("Tag " + key + " failed to load. (Module " + module.displayName + ")", e);
		}
	}
	
	private void applyIterable(BiConsumer<Item, Component> consumer, Iterable<?> iterable) {
		if(key.isEmpty())
			throw new RuntimeException("Multi-item Hints need a defined key (Module " + module.displayName + ")");
		
		for(Object inObj : iterable)
			applyObject(consumer, inObj);
	}
	
	private void applyObject(BiConsumer<Item, Component> consumer, Object obj) {
		if(obj instanceof ItemLike itemLike)
			applyTo(consumer, itemLike);

		else throw new RuntimeException("Invalid hint object on Module " + module.displayName + " - " + obj);
	}

	private void applyTo(BiConsumer<Item, Component> consumer, ItemLike itemLike) {
		var extra = this.extraContent.stream().map(Supplier::get).toArray(Object[]::new);
		if(key.isEmpty())
			HintManager.hintItem(consumer, itemLike, extra);
		else 
			HintManager.hintItem(consumer, itemLike, key, extra);
	}


}
