package vazkii.zeta.module;

import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import vazkii.zeta.Zeta;

/**
 * @see vazkii.zeta.module.ZetaModuleManager for a good way to obtain these
 */
public class ZetaCategory {
	public final String name;
	public final Supplier<ItemStack> icon;
	public final @Nullable String requiredMod;

	public ZetaCategory(String name, Supplier<ItemStack> icon, @Nullable String requiredMod) {
		this.name = name;
		this.icon = icon;
		this.requiredMod = requiredMod;
	}

	public ZetaCategory(String name, Item icon, @Nullable String requiredMod) {
		this(name, () -> new ItemStack(icon), requiredMod);
	}

	public ZetaCategory(String name, Item icon) {
		this(name, icon, null);
	}

	public static ZetaCategory unknownCategory(String id) {
		return new ZetaCategory(id, () -> new ItemStack(Items.PAPER), null);
	}

	public boolean isAddon() {
		return requiredMod != null && !requiredMod.isEmpty();
	}

	public boolean modsLoaded(Zeta z) {
		return !isAddon() || z.isModLoaded(requiredMod);
	}

	@Override
	public String toString() {
		return "ZetaCategory{" + name + "}";
	}

	//Intentionally does not override equals/hashcode (object identity comparison)
}
