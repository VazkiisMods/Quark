package vazkii.quark.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import vazkii.quark.content.tools.module.AmbientDiscsModule;

@Mixin(JukeboxBlockEntity.class)
public class JukeboxBlockEntityMixin {

	@Inject(method = "load", at = @At("TAIL"))
	public void load(CompoundTag nbt, CallbackInfo info) {
		AmbientDiscsModule.Client.onJukeboxLoad((JukeboxBlockEntity) (Object) this);
	}
	
}
