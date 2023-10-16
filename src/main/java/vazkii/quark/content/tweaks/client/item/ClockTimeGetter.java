package vazkii.quark.content.tweaks.client.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.zeta.util.ItemNBTHelper;

public class ClockTimeGetter {

	private static final String TAG_CALCULATED = "quark:clock_calculated";
	
	public static void tickClock(ItemStack stack) {
		boolean calculated = isCalculated(stack);
		if(!calculated)
			ItemNBTHelper.setBoolean(stack, TAG_CALCULATED, true);
	}

	static boolean isCalculated(ItemStack stack) {
		return stack.hasTag() && ItemNBTHelper.getBoolean(stack, TAG_CALCULATED, false);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Impl implements ItemPropertyFunction {
		
		private double rotation;
		private double rota;
		private long lastUpdateTick;
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public float call(@Nonnull ItemStack stack, @Nullable ClientLevel worldIn, @Nullable LivingEntity entityIn, int id) {
			if(!isCalculated(stack))
				return 0F;
			
			boolean carried = entityIn != null;
			Entity entity = carried ? entityIn : stack.getFrame();

			if(worldIn == null && entity != null && entity.level instanceof ClientLevel)
				worldIn = (ClientLevel) entity.level;

			if(worldIn == null)
				return 0F;
			else {
				double angle;

				if (worldIn.dimensionType().natural())
					angle = worldIn.getTimeOfDay(1F); // getCelestrialAngleByTime
				else
					angle = Math.random();

				angle = wobble(worldIn, angle);
				return (float) angle;
			}
		}
		
		private double wobble(Level world, double time) {
			long gameTime = world.getGameTime();
			if(gameTime != lastUpdateTick) {
				lastUpdateTick = gameTime;
				double d0 = time - rotation;
				d0 = Mth.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
				rota += d0 * 0.1D;
				rota *= 0.9D;
				rotation = Mth.positiveModulo(rotation + rota, 1.0D);
			}

			return rotation;
		}
		
	}

}
