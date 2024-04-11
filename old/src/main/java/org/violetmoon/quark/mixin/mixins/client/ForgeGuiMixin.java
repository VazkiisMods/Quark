package org.violetmoon.quark.mixin.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.client.module.ElytraIndicatorModule;

@Mixin(ForgeGui.class)
public class ForgeGuiMixin {

	@ModifyExpressionValue(method = "renderArmor", at = @At(value = "CONSTANT", args = "intValue=20"), remap = false)
	private static int renderArmor(int original) {
		ElytraIndicatorModule module = Quark.ZETA.modules.get(ElytraIndicatorModule.class);
		return module == null ? original : module.getArmorLimit(original);
	}

}
