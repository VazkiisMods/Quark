package vazkii.zeta.module;

import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.zeta.Zeta;

/**
 * @see vazkii.zeta.module.ZetaModuleManager for a good way to obtain these
 */
public class ZetaCategory {
	private final String id;
	private final Supplier<ItemStack> icon;
	private final @Nullable String requiredMod;

	public ZetaCategory(String id, Supplier<ItemStack> icon, @Nullable String requiredMod) {
		this.id = id;
		this.icon = icon;
		this.requiredMod = requiredMod;
	}

	public ZetaCategory(String id, Item icon, @Nullable String requiredMod) {
		this(id, () -> new ItemStack(icon), requiredMod);
	}

	public ZetaCategory(String id, Item icon) {
		this(id, icon, null);
	}

	public static ZetaCategory unknownCategory(String id) {
		return new ZetaCategory(id, () -> new ItemStack(Items.PAPER), null);
	}

	public String id() {
		return id;
	}

	public Supplier<ItemStack> icon() {
		return icon;
	}

	public @Nullable String requiredMod() {
		return requiredMod;
	}

	public boolean modsLoaded(Zeta z) {
		return requiredMod == null || requiredMod.isEmpty() || z.isModLoaded(requiredMod);
	}

	//TODO: temp for ~compat with quark's categories (which take Item, not ItemStack)
	@Deprecated
	public Item getItem() {
		return icon().get().getItem();
	}

	//TODO: compat
	@Deprecated
	public @Nullable ModuleCategory legacy() {
		return ModuleCategory.getCategory(id);
	}

	//Intentionally does not override equals/hashcode (object identity compare)
}
