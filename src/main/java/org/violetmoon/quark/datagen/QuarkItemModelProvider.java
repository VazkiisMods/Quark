package org.violetmoon.quark.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.automation.module.ChuteModule;
import org.violetmoon.quark.content.tools.module.AbacusModule;

public class QuarkItemModelProvider extends ItemModelProvider {
    public QuarkItemModelProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper){
        super(packOutput, Quark.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //Automation
        //etc
        //Tools
        basicItem(AbacusModule.abacus);
    }
}
