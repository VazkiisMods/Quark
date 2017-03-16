package vazkii.quark.base.client;

import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import vazkii.quark.vanity.client.emotes.base.EmoteHandler;

public class ModKeybinds {

	public static HashMap<KeyBinding, String> emoteKeys = new HashMap();
	
	public static KeyBinding lockKey = null;
	public static KeyBinding autoJumpKey = null;
	public static KeyBinding changeHotbarKey = null;
	
	public static void initEmoteKeybinds() {
		for(String emoteName : EmoteHandler.emoteMap.keySet()) {
			KeyBinding key = init("emote." + emoteName, 0);
			emoteKeys.put(key, emoteName);
		}
	}
	
	public static void initLockKey() {
		lockKey = init("lockBuilding", Keyboard.KEY_L);
	}
	
	public static void initAutoJumpKey() {
		autoJumpKey = init("toggleAutojump", Keyboard.KEY_B);
	}
	
	public static void initChangeHotbarKey() {
		changeHotbarKey = init("changeHotbar", Keyboard.KEY_X);
	}
	
	public static KeyBinding init(String s, int key) {
		KeyBinding kb = new KeyBinding("quark.keybind." + s, key, "quark.gui.keygroup");;
		ClientRegistry.registerKeyBinding(kb);
		return kb;
	}
	
}
