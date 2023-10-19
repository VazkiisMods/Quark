package vazkii.quark.addons.oddities.module;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import vazkii.quark.addons.oddities.block.CrateBlock;
import vazkii.quark.addons.oddities.block.be.CrateBlockEntity;
import vazkii.quark.addons.oddities.client.screen.CrateScreen;
import vazkii.quark.addons.oddities.inventory.CrateMenu;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZClientSetup;

@LoadModule(category = "oddities")
public class CrateModule extends QuarkModule {

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

    @LoadEvent
    @OnlyIn(Dist.CLIENT)
    public final void clientSetup(ZClientSetup event) {
        MenuScreens.register(menuType, CrateScreen::new);
    }

}
