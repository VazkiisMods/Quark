package vazkii.quark.tweaks.client.item;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.tweaks.feature.CompassesWorkEverywhere;

public class CompassAngleGetter implements IItemPropertyGetter {

	private static final String TAG_CALCULATED = "quark:compass_calculated";
	private static final String TAG_WAS_IN_NETHER = "quark:compass_in_nether";
	private static final String TAG_POSITION_SET = "quark:compass_position_set";
	private static final String TAG_NETHER_TARGET_X = "quark:nether_x";
	private static final String TAG_NETHER_TARGET_Z = "quark:nether_z";

	double rotation;
	double rota;
	long lastUpdateTick;
	
	public static void tickCompass(EntityPlayer player, ItemStack stack) {
		boolean calculated = isCalculated(stack);
		boolean nether = player.world.provider.getDimensionType() == DimensionType.NETHER; 
		if(calculated) {
			boolean wasInNether = ItemNBTHelper.getBoolean(stack, TAG_WAS_IN_NETHER, false);
			if(nether && !wasInNether) {
				System.out.println("SET");
				BlockPos pos = player.getPosition();
				ItemNBTHelper.setInt(stack, TAG_NETHER_TARGET_X, pos.getX());
				ItemNBTHelper.setInt(stack, TAG_NETHER_TARGET_Z, pos.getZ());
				ItemNBTHelper.setBoolean(stack, TAG_WAS_IN_NETHER, true);
				ItemNBTHelper.setBoolean(stack, TAG_POSITION_SET, true);
			} else if(!nether && wasInNether) {
				System.out.println("RESET");
				ItemNBTHelper.setBoolean(stack, TAG_WAS_IN_NETHER, false);
				ItemNBTHelper.setBoolean(stack, TAG_POSITION_SET, false);
			}
		} else {
			ItemNBTHelper.setBoolean(stack, TAG_CALCULATED, true);
			ItemNBTHelper.setBoolean(stack, TAG_WAS_IN_NETHER, nether);
		}
	}
	
	static boolean isCalculated(ItemStack stack) {
		return stack.hasTagCompound() && ItemNBTHelper.getBoolean(stack, TAG_CALCULATED, false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
		if(entityIn == null && !stack.isOnItemFrame())
			return 0F;
		
		if(CompassesWorkEverywhere.enableCompassNerf && (!stack.hasTagCompound() || !ItemNBTHelper.getBoolean(stack, TAG_CALCULATED, false)))
			return 0F;

		boolean carried = entityIn != null;
		Entity entity = carried ? entityIn : stack.getItemFrame();

		if(worldIn == null)
			worldIn = entity.world;

		double angle;

		boolean calculate = false;
		BlockPos target = new BlockPos(0, 0, 0);
		
		if(worldIn.provider.isSurfaceWorld()) {
			calculate = true;
			target = worldIn.getSpawnPoint();
		} else if(worldIn.provider.getDimensionType() == DimensionType.THE_END && CompassesWorkEverywhere.enableEnd)
			calculate = true;
		else if(worldIn.provider.getDimensionType() == DimensionType.NETHER && isCalculated(stack) && CompassesWorkEverywhere.enableNether) {
			boolean set = ItemNBTHelper.getBoolean(stack, TAG_POSITION_SET, false);
			if(set) {
				int x = ItemNBTHelper.getInt(stack, TAG_NETHER_TARGET_X, 0);
				int z = ItemNBTHelper.getInt(stack, TAG_NETHER_TARGET_Z, 0);
				calculate = true;
				target = new BlockPos(x, 0, z);
			}
		}
		
		if(calculate) {
			double d1 = carried ? entity.rotationYaw : getFrameRotation((EntityItemFrame)entity);
			d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
			double d2 = getAngleToPosition(entity, target) / (Math.PI * 2D);
			angle = 0.5D - (d1 - 0.25D - d2);
		} else angle = Math.random();

		if (carried)
			angle = wobble(worldIn, angle);

		return MathHelper.positiveModulo((float) angle, 1.0F);
	}

	@SideOnly(Side.CLIENT)
	private double wobble(World worldIn, double angle) {
		if(worldIn.getTotalWorldTime() != lastUpdateTick) {
			lastUpdateTick = worldIn.getTotalWorldTime();
			double d0 = angle - rotation;
			d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
			rota += d0 * 0.1D;
			rota *= 0.8D;
			rotation = MathHelper.positiveModulo(rotation + rota, 1.0D);
		}

		return rotation;
	}

	@SideOnly(Side.CLIENT)
	private double getFrameRotation(EntityItemFrame frame) {
		return MathHelper.wrapDegrees(180 + frame.facingDirection.getHorizontalIndex() * 90);
	}

	@SideOnly(Side.CLIENT)
	private double getAngleToPosition(Entity entity, BlockPos blockpos) {
		return Math.atan2(blockpos.getZ() - entity.posZ, blockpos.getX() - entity.posX);
	}

}
