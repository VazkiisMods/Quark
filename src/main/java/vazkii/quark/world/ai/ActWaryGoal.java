/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 11, 2019, 17:44 AM (EST)]
 */
package vazkii.quark.world.ai;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BooleanSupplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import vazkii.quark.tweaks.base.MutableVectorHolder;
import vazkii.quark.world.entity.StonelingEntity;

public class ActWaryGoal extends WaterAvoidingRandomWalkingGoal {

	private final StonelingEntity stoneling;

	private final BooleanSupplier scaredBySuddenMovement;

	private final double range;

	private boolean startled;

	private final Map<PlayerEntity, MutableVectorHolder> lastPositions = new WeakHashMap<>();
	private final Map<PlayerEntity, MutableVectorHolder> lastSpeeds = new WeakHashMap<>();

	public ActWaryGoal(StonelingEntity stoneling, double speed, double range, BooleanSupplier scaredBySuddenMovement) {
		super(stoneling, speed, 1F);
		this.stoneling = stoneling;
		this.range = range;
		this.scaredBySuddenMovement = scaredBySuddenMovement;
	}

	private static void updateMotion(MutableVectorHolder holder, double x, double y, double z) {
		holder.x = x;
		holder.y = y;
		holder.z = z;
	}

	private static void updatePos(MutableVectorHolder holder, Entity entity) {
		Vec3d pos = entity.getPositionVec();
		holder.x = pos.x;
		holder.y = pos.y;
		holder.z = pos.z;
	}

	private static MutableVectorHolder initPos(PlayerEntity p) {
		MutableVectorHolder holder = new MutableVectorHolder();
		updatePos(holder, p);
		return holder;
	}

	public void startle() {
		startled = true;
	}

	public boolean isStartled() {
		return startled;
	}

	protected boolean shouldApplyPath() {
		return super.shouldExecute();
	}

	@Override
	public void tick() {
		if (stoneling.getNavigator().noPath() && shouldApplyPath())
			startExecuting();
	}

	@Override
	public boolean shouldContinueExecuting() {
		return shouldExecute();
	}

	@Override
	public void resetTask() {
		stoneling.getNavigator().clearPath();
	}

	@Override
	public boolean shouldExecute() {
		if (startled || stoneling.isPlayerMade())
			return false;

		List<PlayerEntity> playersAround = stoneling.world.getEntitiesWithinAABB(PlayerEntity.class, stoneling.getBoundingBox().grow(range),
				(player) -> player != null && !player.abilities.isCreativeMode && player.getDistanceSq(stoneling) < range * range);

		if (playersAround.isEmpty())
			return false;

		for (PlayerEntity player : playersAround) {
			if (player.isDiscrete()) {
				if (scaredBySuddenMovement.getAsBoolean()) {
					MutableVectorHolder lastSpeed = lastSpeeds.computeIfAbsent(player, p -> new MutableVectorHolder());
					MutableVectorHolder lastPos = lastPositions.computeIfAbsent(player, ActWaryGoal::initPos);
					Vec3d pos = player.getPositionVec();

					double dX = pos.x - lastPos.x;
					double dY = pos.y - lastPos.y;
					double dZ = pos.z - lastPos.z;

					double xDisplacement = dX - lastSpeed.x;
					double yDisplacement = dY - lastSpeed.y;
					double zDisplacement = dZ - lastSpeed.z;

					updateMotion(lastSpeed, dX, dY, dZ);
					updatePos(lastPos, player);

					double displacementSq = xDisplacement * xDisplacement +
							yDisplacement * yDisplacement +
							zDisplacement * zDisplacement;

					if (displacementSq < 0.01)
						return true;

					startled = true;
					return false;
				}
			} else {
				startled = true;
				return false;
			}
		}

		return true;
	}
}
