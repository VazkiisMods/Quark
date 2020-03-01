/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 20, 2019, 01:58 AM (EST)]
 */
package vazkii.quark.tweaks.module;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlimeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import vazkii.quark.base.handler.OverrideRegistryHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.tweaks.base.MutableVectorHolder;
import vazkii.quark.tweaks.block.SpringySlimeBlock;

@LoadModule(category = ModuleCategory.TWEAKS)
public class SpringySlimeModule extends Module {

	@Override
	public void construct() {
		SpringySlimeBlock block = new SpringySlimeBlock(Block.Properties.from(Blocks.SLIME_BLOCK));

		OverrideRegistryHandler.registerBlock(block, "slime_block");

		BlockItem item = new BlockItem(block, new Item.Properties().group(ItemGroup.DECORATIONS));

		OverrideRegistryHandler.registerItem(item, "slime_block");
	}

	private static final ThreadLocal<MutableVectorHolder> motionRecorder = ThreadLocal.withInitial(MutableVectorHolder::new);

	public static void recordMotion(Entity entity) {
		motionRecorder.get().importFrom(entity.getMotion());
	}

	public static void collideWithSlimeBlock(BlockPos pos, Entity entity) {
		if (entity instanceof AbstractArrowEntity) {

			Vec3d motion = entity.getMotion();
			double motionX = motion.x;
			double motionY = motion.y;
			double motionZ = motion.z;

			Vec3d epos = entity.getPositionVector();
			Direction sideHit = Direction.getFacingFromVector(
					(float) (epos.x + motionX) - (pos.getX() + 0.5f),
					(float) (epos.y + motionY) - (pos.getY() + 0.5f),
					(float) (epos.z + motionZ) - (pos.getZ() + 0.5f));

			switch (sideHit.getAxis()) {
				case X:
					if (Math.abs(motionX) < 0.1)
						return;
					motionX = 0.8 * Math.min(Math.abs(motionX), 0.25) * sideHit.getXOffset();
					break;
				case Y:
					if (Math.abs(motionY) < 0.1)
						return;
					motionY = 0.8 * Math.min(Math.abs(motionY), 0.25) * sideHit.getYOffset();
					break;
				case Z:
					if (Math.abs(motionZ) < 0.1)
						return;
					motionZ = 0.8 * Math.min(Math.abs(motionZ), 0.25) * sideHit.getZOffset();
					break;
			}

			entity.setMotion(motionX, motionY, motionZ);

			((AbstractArrowEntity) entity).inGround = true;
		}
	}

	public static void onEntityCollision(Entity entity, Vec3d attempted, Vec3d actual) {
		if (!ModuleLoader.INSTANCE.isModuleEnabled(SpringySlimeModule.class))
			return;

		if (entity.isDiscrete() || (entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.isFlying))
			return;

		double attemptedX = attempted.x;
		double attemptedY = attempted.y;
		double attemptedZ = attempted.z;
		double dX = actual.x;
		double dY = actual.y;
		double dZ = actual.z;

		double height = entity.getHeight();
		double width = entity.getWidth();
		
		Vec3d pos = entity.getPositionVec();

		double minX = pos.x - width / 2;
		double minY = pos.y;
		double minZ = pos.z - width / 2;
		double maxX = pos.x + width / 2;
		double maxY = pos.y + height;
		double maxZ = pos.z + width / 2;

		if (attemptedX != dX)
			applyForAxis(entity, Axis.X, minX, minY, minZ, maxX, maxY, maxZ, dX, attemptedX);

		if (attemptedY != dY)
			applyForAxis(entity, Axis.Y, minX, minY, minZ, maxX, maxY, maxZ, dY, attemptedY);

		if (attemptedZ != dZ)
			applyForAxis(entity, Axis.Z, minX, minY, minZ, maxX, maxY, maxZ, dZ, attemptedZ);
	}

	private static double axial(Axis axis, double we, double ud, double ns) {
		switch (axis) {
			case X:
				return we;
			case Y:
				return ud;
			default:
				return ns;
		}
	}

	private static void applyForAxis(Entity entity, Axis axis, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double dV, double attemptedV) {
		double baseValue = dV < 0 ? axial(axis, minX, minY, minZ) : axial(axis, maxX, maxY, maxZ);
		double clampedAttempt = attemptedV;
		if (Math.abs(attemptedV) > Math.abs(dV) + 1)
			clampedAttempt = dV + Math.signum(dV);
		double v1 = baseValue + dV;
		double v2 = baseValue + clampedAttempt;
		double minV = Math.min(v1, v2);
		double maxV = Math.max(v1, v2);
		Direction impactedSide = Direction.getFacingFromAxis(dV < 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, axis);

		int lowXBound = (int) Math.floor(axial(axis, minV, minX, minX));
		int highXBound = (int) Math.floor(axial(axis, maxV, maxX, maxX));
		int lowYBound = (int) Math.floor(axial(axis, minY, minV, minY));
		int highYBound = (int) Math.floor(axial(axis, maxY, maxV, maxY));
		int lowZBound = (int) Math.floor(axial(axis, minZ, minZ, minV));
		int highZBound = (int) Math.floor(axial(axis, maxZ, maxZ, maxV));

		boolean restoredZ = false;
		for (BlockPos position : BlockPos.getAllInBoxMutable(lowXBound, lowYBound, lowZBound,
				highXBound, highYBound, highZBound)) {
			restoredZ = applyCollision(entity, position, impactedSide, restoredZ);
		}
	}

	private static boolean applyCollision(Entity entity, BlockPos position, Direction impacted, boolean restoredMotion) {
		BlockState state = entity.world.getBlockState(position);
		if (isSlime(state)) {
			if (impacted == Direction.UP && entity instanceof ItemEntity)
				entity.onGround = false;

			Vec3d motion = entity.getMotion();
			double motionX = motion.x;
			double motionY = motion.y;
			double motionZ = motion.z;

			switch (impacted.getAxis()) {
				case X:
					if (!restoredMotion) {
						restoredMotion = true;
						motionX = motionRecorder.get().x;
					}
					motionX = Math.abs(motionX) * impacted.getXOffset();
					if (!(entity instanceof LivingEntity))
						motionX *= 0.8;
					break;
				case Y:
					if (!restoredMotion) {
						restoredMotion = true;
						motionY = motionRecorder.get().y;
					}
					motionY = Math.abs(motionY) * impacted.getYOffset();
					if (!(entity instanceof LivingEntity))
						motionY *= 0.8;
					break;
				case Z:
					if (!restoredMotion) {
						restoredMotion = true;
						motionZ = motionRecorder.get().z;
					}
					motionZ = Math.abs(motionZ) * impacted.getZOffset();
					if (!(entity instanceof LivingEntity))
						motionZ *= 0.8;
					break;
			}

			entity.setMotion(motionX, motionY, motionZ);
		}


		return restoredMotion;
	}
	
	private static boolean isSlime(BlockState state) {
		Block block = state.getBlock();
		return block instanceof SlimeBlock;
	}
}
