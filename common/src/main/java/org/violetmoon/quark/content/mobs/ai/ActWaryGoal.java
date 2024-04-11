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
package org.violetmoon.quark.content.mobs.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import org.violetmoon.quark.base.util.MutableVectorHolder;
import org.violetmoon.quark.content.mobs.entity.Stoneling;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BooleanSupplier;

public class ActWaryGoal extends WaterAvoidingRandomStrollGoal {

	private final Stoneling stoneling;

	private final BooleanSupplier scaredBySuddenMovement;

	private final double range;

	private boolean startled;

	private final Map<Player, MutableVectorHolder> lastPositions = new WeakHashMap<>();
	private final Map<Player, MutableVectorHolder> lastSpeeds = new WeakHashMap<>();

	public ActWaryGoal(Stoneling stoneling, double speed, double range, BooleanSupplier scaredBySuddenMovement) {
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
		Vec3 pos = entity.position();
		holder.x = pos.x;
		holder.y = pos.y;
		holder.z = pos.z;
	}

	private static MutableVectorHolder initPos(Player p) {
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
		return super.canUse();
	}

	@Override
	public void tick() {
		if(stoneling.getNavigation().isDone() && shouldApplyPath())
			start();
	}

	@Override
	public boolean canContinueToUse() {
		return canUse();
	}

	@Override
	public void stop() {
		stoneling.getNavigation().stop();
	}

	@Override
	public boolean canUse() {
		if(startled || stoneling.isPlayerMade())
			return false;

		List<Player> playersAround = stoneling.level().getEntitiesOfClass(Player.class, stoneling.getBoundingBox().inflate(range),
				(player) -> player != null && !player.getAbilities().instabuild && player.distanceToSqr(stoneling) < range * range);

		if(playersAround.isEmpty())
			return false;

		for(Player player : playersAround) {
			if(player.isDiscrete()) {
				if(scaredBySuddenMovement.getAsBoolean()) {
					MutableVectorHolder lastSpeed = lastSpeeds.computeIfAbsent(player, p -> new MutableVectorHolder());
					MutableVectorHolder lastPos = lastPositions.computeIfAbsent(player, ActWaryGoal::initPos);
					Vec3 pos = player.position();

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

					if(displacementSq < 0.01)
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
