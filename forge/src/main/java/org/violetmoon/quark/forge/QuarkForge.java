package org.violetmoon.quark.forge;

import org.violetmoon.quark.Quark;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Quark.MOD_ID)
public class QuarkForge {
    public QuarkForge() {
        Quark.init();
    }
}