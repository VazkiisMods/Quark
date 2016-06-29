/**
 * This class was created by <SanAndreasP>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 22:33:44 (GMT)]
 */
package vazkii.quark.decoration.feature;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockCustomChest;
import vazkii.quark.decoration.client.render.RenderTileEntityCustomChest;
import vazkii.quark.decoration.tileentity.TileEntityCustomChest;

import java.util.HashMap;
import java.util.Map;

public class CustomChest extends Feature {

    public static BlockCustomChest custom_chest;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        custom_chest = new BlockCustomChest();

        //TODO: add crafting recipe
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void preInitClient(FMLPreInitializationEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCustomChest.class, new RenderTileEntityCustomChest());
    }

    public enum ChestType {
        NONE(""),
        ACACIA("acacia"),
        BIRCH("birch"),
        DARKOAK("darkoak"),
        JUNGLE("jungle"),
        SPRUCE("spruce");

        public final String name;
        public final ResourceLocation nrmTex;
        public final ResourceLocation dblTex;

        public static final ChestType[] VALID_TYPES;
        public static final Map<String, ChestType> NAME_TO_TYPE;

        ChestType(String name) {
            this.name = name;
            this.nrmTex = new ResourceLocation(LibMisc.PREFIX_MOD + "textures/blocks/chest/" + name + ".png");
            this.dblTex = new ResourceLocation(LibMisc.PREFIX_MOD + "textures/blocks/chest/" + name + "_double.png");
        }

        public static ChestType getType(String type) {
            return NAME_TO_TYPE.containsKey(type) ? NAME_TO_TYPE.get(type) : NONE;
        }

        static {
            VALID_TYPES = new ChestType[] { ACACIA, BIRCH, DARKOAK, JUNGLE, SPRUCE };
            NAME_TO_TYPE = new HashMap<>();
            for( ChestType type : VALID_TYPES )
                NAME_TO_TYPE.put(type.name, type);
        }
    }
}
