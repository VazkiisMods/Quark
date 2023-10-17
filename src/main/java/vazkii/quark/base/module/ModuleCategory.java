package vazkii.quark.base.module;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import vazkii.quark.base.Quark;

@Deprecated
public enum ModuleCategory {

	// Categories
	AUTOMATION("automation", Items.REDSTONE),
	BUILDING("building", Items.BRICKS),
	MANAGEMENT("management", Items.CHEST),
	TOOLS("tools", Items.IRON_PICKAXE),
	TWEAKS("tweaks", Items.NAUTILUS_SHELL),
	WORLD("world", Items.GRASS_BLOCK),
	MOBS("mobs", Items.PIG_SPAWN_EGG),
	CLIENT("client", Items.ENDER_EYE),
	EXPERIMENTAL("experimental", Items.TNT),
	ODDITIES("oddities", Items.CHORUS_FRUIT, Quark.ODDITIES_ID);
	
	public final String name;
	public final Item item;
	public final String requiredMod;
	
	public boolean enabled;
	
	private List<QuarkModule> ownedModules = new ArrayList<>();
	
	ModuleCategory(String name, Item item, String requiredMod) {
		this.name = name;
		this.item = item;
		this.requiredMod = requiredMod;
		this.enabled = true;
	}
	
	ModuleCategory(String name, Item item) {
		this(name, item, null);
	}
	
	public void addModule(QuarkModule module) {
		ownedModules.add(module);
	}
	
	public List<QuarkModule> getOwnedModules() {
		return ownedModules;
	}
	
	public boolean isAddon() {
		return requiredMod != null;
	}

	//ZETA
	public static @Nullable ModuleCategory getCategory(String id) {
		for(ModuleCategory cat : ModuleCategory.values()) {
			if(cat.name.equals(id)) return cat;
		}

		return null;
	}
}
