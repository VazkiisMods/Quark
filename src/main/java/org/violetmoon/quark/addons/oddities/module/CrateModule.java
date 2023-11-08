package org.violetmoon.quark.addons.oddities.module;

import org.violetmoon.quark.addons.oddities.block.CrateBlock;
import org.violetmoon.quark.addons.oddities.block.be.CrateBlockEntity;
import org.violetmoon.quark.addons.oddities.client.screen.CrateScreen;
import org.violetmoon.quark.addons.oddities.inventory.CrateMenu;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.config.Config;
import org.violetmoon.zeta.client.event.load.ZClientSetup;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;

@ZetaLoadModule(category = "oddities")
public class CrateModule extends ZetaModule {

    public static BlockEntityType<CrateBlockEntity> blockEntityType;
    public static MenuType<CrateMenu> menuType;

    @Hint(content = "maxItems")
    public static Block crate;

    @Config
    public static int maxItems = 640;

    @LoadEvent
    public final void register(ZRegister event) {
        crate = new CrateBlock(this);

        menuType = IForgeMenuType.create(CrateMenu::fromNetwork);
	    Quark.ZETA.registry.register(menuType, "crate", Registry.MENU_REGISTRY);

	    blockEntityType = BlockEntityType.Builder.of(CrateBlockEntity::new, crate).build(null);
	    Quark.ZETA.registry.register(blockEntityType, "crate", Registry.BLOCK_ENTITY_TYPE_REGISTRY);
    }



    @ZetaLoadModule(clientReplacement = true)
    public static class Client extends CrateModule {
        @LoadEvent
        public final void clientSetup(ZClientSetup event) {
            MenuScreens.register(menuType, CrateScreen::new);
        }
    }
}
