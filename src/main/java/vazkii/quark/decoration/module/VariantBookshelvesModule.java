package vazkii.quark.decoration.module;

import net.minecraft.block.Blocks;
import vazkii.quark.base.handler.ItemOverrideHandler;
import vazkii.quark.base.module.Config;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.decoration.block.VariantBookshelfBlock;

@LoadModule(category = ModuleCategory.DECORATION)
public class VariantBookshelvesModule extends Module {

    @Config public static boolean changeNames = true;

    @Override
    public void start() {
        for (String type : new String[] { "acacia", "birch", "dark_oak", "jungle", "spruce" }) {
            new VariantBookshelfBlock(type, this);
        }
    }

    @Override
    public void configChanged()
    {
        ItemOverrideHandler.changeBlockLocalizationKey(Blocks.BOOKSHELF, "block.quark.oak_bookshelf", changeNames && enabled);
    }
}
