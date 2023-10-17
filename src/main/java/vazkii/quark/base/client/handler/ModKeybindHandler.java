package vazkii.quark.base.client.handler;

import java.util.function.BiPredicate;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyModifier;
import vazkii.quark.base.client.util.PredicatedKeyBinding;
import vazkii.quark.base.client.util.SortedKeyBinding;
import vazkii.quark.base.client.util.SortedPredicatedKeyBinding;
import vazkii.zeta.event.client.ZKeyMapping;

@OnlyIn(Dist.CLIENT)
public class ModKeybindHandler {

	public static final String MISC_GROUP = "quark.gui.keygroup.misc";
	public static final String INV_GROUP = "quark.gui.keygroup.inv";
	public static final String EMOTE_GROUP = "quark.gui.keygroup.emote";

	public static KeyMapping init(ZKeyMapping event, String s, String key, String group) {
		return init(event, s, key, "key.keyboard.", group, true);
	}

	public static KeyMapping init(ZKeyMapping event, String s, String key, String group, int sortPriority) {
		return init(event, s, key, "key.keyboard.", group, sortPriority, true);
	}

	public static KeyMapping init(ZKeyMapping event, String s, String key, String group, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		return init(event, s, key, "key.keyboard.", group, true);
	}

	public static KeyMapping init(ZKeyMapping event, String s, String key, String group, int sortPriority, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		return init(event, s, key, "key.keyboard.", group, sortPriority, true);
	}

	public static KeyMapping initMouse(ZKeyMapping event, String s, int key, String group) {
		return init(event, s, Integer.toString(key), "key.mouse.", group, true);
	}

	public static KeyMapping initMouse(ZKeyMapping event, String s, int key, String group, int sortPriority) {
		return init(event, s, Integer.toString(key), "key.mouse.", group, sortPriority, true);
	}

	public static KeyMapping initMouse(ZKeyMapping event, String s, int key, String group, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		return init(event, s, Integer.toString(key), "key.mouse.", group, true, allowed);
	}

	public static KeyMapping initMouse(ZKeyMapping event, String s, int key, String group, int sortPriority, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		return init(event, s, Integer.toString(key), "key.mouse.", group, sortPriority, true, allowed);
	}

	public static KeyMapping init(ZKeyMapping event, String s, String key, String keyType, String group, boolean prefix) {
		KeyMapping kb = new KeyMapping(prefix ? ("quark.keybind." + s) : s, (keyType.contains("mouse") ? Type.MOUSE : Type.KEYSYM),
				(key == null ? InputConstants.UNKNOWN :
						InputConstants.getKey(keyType + key)).getValue(),
				group);
		event.register(kb);
		return kb;
	}

	public static KeyMapping init(ZKeyMapping event, String s, String key, String keyType, String group, int sortPriority, boolean prefix) {
		KeyMapping kb = new SortedKeyBinding(prefix ? ("quark.keybind." + s) : s, (keyType.contains("mouse") ? Type.MOUSE : Type.KEYSYM),
				(key == null ? InputConstants.UNKNOWN :
						InputConstants.getKey(keyType + key)).getValue(),
				group, sortPriority);
		event.register(kb);
		return kb;
	}

	public static KeyMapping init(ZKeyMapping event, String s, String key, String keyType, String group, int sortPriority, boolean prefix, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		KeyMapping kb = new SortedPredicatedKeyBinding(prefix ? ("quark.keybind." + s) : s, (keyType.contains("mouse") ? Type.MOUSE : Type.KEYSYM),
				(key == null ? InputConstants.UNKNOWN :
						InputConstants.getKey(keyType + key)).getValue(),
				group, sortPriority, allowed);
		event.register(kb);
		return kb;
	}

	public static KeyMapping init(ZKeyMapping event, String s, String key, String keyType, String group, boolean prefix, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		KeyMapping kb = new PredicatedKeyBinding(prefix ? ("quark.keybind." + s) : s, (keyType.contains("mouse") ? Type.MOUSE : Type.KEYSYM),
				(key == null ? InputConstants.UNKNOWN :
						InputConstants.getKey(keyType + key)).getValue(),
				group, allowed);
		event.register(kb);
		return kb;
	}

}
