package org.violetmoon.quark.fabric;

import org.violetmoon.quark.Quark;
import net.fabricmc.api.ModInitializer;

public class QuarkFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Quark.init();
    }
}