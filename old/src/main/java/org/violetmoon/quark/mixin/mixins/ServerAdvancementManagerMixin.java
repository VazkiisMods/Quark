package org.violetmoon.quark.mixin.mixins;

import com.google.gson.JsonElement;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import org.violetmoon.quark.content.tweaks.module.AutomaticRecipeUnlockModule;

import java.util.Map;

@Mixin(value = ServerAdvancementManager.class, priority = 1001)
public class ServerAdvancementManagerMixin {
	@Inject(
		method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementList;<init>()V"),
		locals = LocalCapture.CAPTURE_FAILSOFT,
		require = 0
	)
	private void removeRecipeAdvancements(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler,
			CallbackInfo ci, Map<ResourceLocation, Advancement.Builder> builders) {
		AutomaticRecipeUnlockModule.removeRecipeAdvancements(builders);
	}

}
