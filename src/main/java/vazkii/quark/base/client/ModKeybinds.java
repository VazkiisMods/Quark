package vazkii.quark.base.client;

import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import vazkii.quark.base.lib.LibObfuscation;
import vazkii.quark.vanity.client.emotes.EmoteDescriptor;
import vazkii.quark.vanity.client.emotes.EmoteHandler;

public class ModKeybinds {

	public static HashMap<KeyBinding, String> emoteKeys = new HashMap();

	public static BiMap<KeyBinding, IParentedGui> keyboundButtons = HashBiMap.create();

	public static KeyBinding lockKey = null;
	public static KeyBinding autoJumpKey = null;
	public static KeyBinding changeHotbarKey = null;

	public static KeyBinding dropoffKey = null;
	public static KeyBinding playerSortKey = null;
	public static KeyBinding chestSortKey = null;
	public static KeyBinding chestDropoffKey = null;
	public static KeyBinding chestMergeKey = null;
	public static KeyBinding chestRestockKey = null;
	public static KeyBinding chestExtractKey = null;
	
	private static final String MISC_GROUP = "quark.gui.keygroupMisc";
	private static final String INV_GROUP = "quark.gui.keygroupInv";
	private static final String EMOTE_GROUP = "quark.gui.keygroupEmote";

	public static void initEmoteKeybinds() {
		for(String emoteName : EmoteHandler.emoteMap.keySet()) {
			EmoteDescriptor desc = EmoteHandler.emoteMap.get(emoteName);
			KeyBinding key = init(desc.getUnlocalizedName(), 0, EMOTE_GROUP, false);
			emoteKeys.put(key, emoteName);
		}
	}

	public static void initLockKey() {
		lockKey = init("lockBuilding", Keyboard.KEY_K, MISC_GROUP);
	}

	public static void initAutoJumpKey() {
		autoJumpKey = init("toggleAutojump", Keyboard.KEY_B, MISC_GROUP);
	}

	public static void initChangeHotbarKey() {
		changeHotbarKey = init("changeHotbar", Keyboard.KEY_Z, MISC_GROUP);
	}

	public static void initDropoffKey() {
		dropoffKey = initAndButtonBind("dropoff", 0);
	}

	public static void initPlayerSortingKey() {
		playerSortKey = initAndButtonBind("playerSort", 0);
	}

	public static void initChestKeys() {
		chestSortKey = initAndButtonBind("chestSort", 0);
		chestDropoffKey = initAndButtonBind("chestDropoff", 0);
		chestMergeKey = initAndButtonBind("chestMerge", 0);
		chestRestockKey = initAndButtonBind("chestRestock", 0);
		chestExtractKey = initAndButtonBind("chestExtract", 0);
	}

	public static void keybindButton(KeyBinding key, IParentedGui ipg) {
		if(key != null)
			keyboundButtons.put(key, ipg);
	}

	private static KeyBinding initAndButtonBind(String s, int key) {
		KeyBinding kb = init(s, key, INV_GROUP);
		new KeybindButtonHandler(kb);
		return kb;
	}
	
	private static KeyBinding init(String s, int key, String group) {
		return init(s, key, group, true);
	}
	
	private static KeyBinding init(String s, int key, String group, boolean prefix) {
		KeyBinding kb = new KeyBinding(prefix ? ("quark.keybind." + s) : s, key, group);
		ClientRegistry.registerKeyBinding(kb);
		return kb;
	}
	
	public static boolean isKeyDown(KeyBinding keybind) {
		int key = keybind.getKeyCode();
		if(key == 0)
			return false;
		
		if(key < 0) {
			int button = 100 + key;
			return Mouse.isButtonDown(button);
		}
		try {
			return Keyboard.isKeyDown(key);
		} catch(IndexOutOfBoundsException e) {
			return false;
		}
	}

	private static class KeybindButtonHandler {

		final KeyBinding ref;
		boolean down;

		public KeybindButtonHandler(KeyBinding ref) {
			this.ref = ref;
			MinecraftForge.EVENT_BUS.register(this);
		}

		@SubscribeEvent
		public void onKeyinput(KeyboardInputEvent.Post event) {
			boolean wasDown = down;
			down = isKeyDown(ref);
			
			if(!wasDown && down && keyboundButtons.containsKey(ref)) {
				IParentedGui ipg = keyboundButtons.get(ref);
				GuiScreen curr = Minecraft.getMinecraft().currentScreen;
				if(curr == ipg.getParent()) {
					List<GuiButton> buttonList = ReflectionHelper.getPrivateValue(GuiScreen.class, curr, LibObfuscation.BUTTON_LIST);
					GuiScreenEvent.ActionPerformedEvent.Pre postEvent = new GuiScreenEvent.ActionPerformedEvent.Pre(curr, (GuiButton) ipg, buttonList);
					MinecraftForge.EVENT_BUS.post(postEvent);
				}
			}
		}

	}

}

