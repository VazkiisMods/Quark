package vazkii.quark.content.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.handler.RayTraceHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "client")
public class LongRangePickBlockModule extends QuarkModule {

	public static boolean staticEnabled;

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		staticEnabled = enabled;
	}

	@OnlyIn(Dist.CLIENT)
	public static HitResult transformHitResult(HitResult hitResult) {
		if(staticEnabled) {
			Minecraft mc = Minecraft.getInstance();
			Player player = mc.player;
			Level level = mc.level;

			if(hitResult != null) {
				if(hitResult instanceof BlockHitResult bhr && !level.getBlockState(bhr.getBlockPos()).isAir())
					return hitResult;

				if(hitResult instanceof EntityHitResult ehr)
					return hitResult;
			}

			HitResult result = RayTraceHandler.rayTrace(player, level, player, Block.OUTLINE, Fluid.NONE, 200);
			if(result != null && result.getType() == Type.BLOCK)
				return result;
		}

		return hitResult;
	}

}
