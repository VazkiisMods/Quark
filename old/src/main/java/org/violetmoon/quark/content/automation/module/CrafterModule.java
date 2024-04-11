package org.violetmoon.quark.content.automation.module;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.extensions.IForgeMenuType;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.automation.block.CrafterBlock;
import org.violetmoon.quark.content.automation.block.be.CrafterBlockEntity;
import org.violetmoon.quark.content.automation.client.screen.CrafterScreen;
import org.violetmoon.quark.content.automation.inventory.CrafterMenu;
import org.violetmoon.zeta.client.event.load.ZClientSetup;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

//fixme Fix crafter PITA atm and frankly not worth delaying a stable release over
@ZetaLoadModule(category = "automation")
public class CrafterModule extends ZetaModule {

    public static Block block;
    public static MenuType<CrafterMenu> menuType;
    public static BlockEntityType<CrafterBlockEntity> blockEntityType;

    Block crafter;

    @Config(description = "Setting this to true will change the Crafter to use Emi's original design instead of Mojang's.\n"
    		+ "Emi's design allows only one item per slot, instead of continuing to fill it round robin.\n"
    		+ "If this is enabled, Allow Items While Powered should also be set to false for the full design.")
    public static boolean useEmiLogic = false;

    @Config(description =  "Set to false to allow items to be inserted into the Crafter even while it's powered.")
    public static boolean allowItemsWhilePowered = true;

    @LoadEvent
    public final void register(ZRegister event) {
        crafter = block = new CrafterBlock("crafter", this,
                Block.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .strength(2.5F)
                        .sound(SoundType.WOOD)
                        .ignitedByLava()
        );

        menuType = IForgeMenuType.create(CrafterMenu::fromNetwork);
        Quark.ZETA.registry.register(menuType, "crafter", Registries.MENU);

        blockEntityType = BlockEntityType.Builder.of(CrafterBlockEntity::new, crafter).build(null);
        Quark.ZETA.registry.register(blockEntityType, "crafter", Registries.BLOCK_ENTITY_TYPE);
    }

    @LoadEvent
    public final void clientSetup(ZClientSetup event) {
        MenuScreens.register(menuType, CrafterScreen::new);
    }
}
