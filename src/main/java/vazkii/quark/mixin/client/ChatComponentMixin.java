package vazkii.quark.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.quark.content.management.module.ItemSharingModule;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

	@WrapOperation(method = "render", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Font;drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/util/FormattedCharSequence;FFI)I"))
	private int drawItems(Font instance, PoseStack poseStack, FormattedCharSequence sequence, float x, float y, int color, Operation<Integer> original) {
		ItemSharingModule.Client.renderItemForMessage(poseStack, sequence, x, y, color);

		return original.call(instance, poseStack, sequence, x, y, color);
	}
}
