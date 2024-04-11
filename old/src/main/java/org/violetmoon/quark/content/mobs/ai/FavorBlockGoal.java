/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 27, 2019, 13:45 AM (EST)]
 */
package org.violetmoon.quark.content.mobs.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.function.Predicate;

public class FavorBlockGoal extends Goal {

	private final PathfinderMob creature;
	private final double movementSpeed;
	private final Predicate<BlockState> targetBlock;

	protected int runDelay;
	private int timeoutCounter;
	private int maxStayTicks;

	protected BlockPos destinationBlock = BlockPos.ZERO;

	public FavorBlockGoal(PathfinderMob creature, double speed, Predicate<BlockState> predicate) {
		this.creature = creature;
		this.movementSpeed = speed;
		this.targetBlock = predicate;
		setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
	}

	public FavorBlockGoal(PathfinderMob creature, double speed, TagKey<Block> tag) {
		this(creature, speed, (state) -> state.is(tag));
	}

	public FavorBlockGoal(PathfinderMob creature, double speed, Block block) {
		this(creature, speed, (state) -> state.getBlock() == block);
	}

	@Override
	public boolean canUse() {
		if(runDelay > 0) {
			--runDelay;
			return false;
		} else {
			runDelay = 200 + creature.getRandom().nextInt(200);
			return searchForDestination();
		}
	}

	@Override
	public boolean canContinueToUse() {
		return timeoutCounter >= -maxStayTicks && timeoutCounter <= 1200 && targetBlock.test(creature.level().getBlockState(destinationBlock));
	}

	@Override
	public void start() {
		creature.getNavigation().moveTo(destinationBlock.getX() + 0.5, destinationBlock.getY() + 1, destinationBlock.getZ() + 0.5, movementSpeed);
		timeoutCounter = 0;
		maxStayTicks = creature.getRandom().nextInt(creature.getRandom().nextInt(1200) + 1200) + 1200;
	}

	@Override
	public void tick() {
		if(creature.distanceToSqr(new Vec3(destinationBlock.getX(), destinationBlock.getY(), destinationBlock.getZ()).add(0.5, 1.5, 0.5)) > 1.0D) {
			++timeoutCounter;

			if(timeoutCounter % 40 == 0)
				creature.getNavigation().moveTo(destinationBlock.getX() + 0.5D, destinationBlock.getY() + 1, destinationBlock.getZ() + 0.5D, movementSpeed);
		} else {
			--timeoutCounter;
		}
	}

	private boolean searchForDestination() {
		double followRange = creature.getAttribute(Attributes.FOLLOW_RANGE).getValue();
		Vec3 cpos = creature.position();
		double xBase = cpos.x;
		double yBase = cpos.y;
		double zBase = cpos.z;

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for(int yShift = 0;
				yShift <= 1;
				yShift = yShift > 0 ? -yShift : 1 - yShift) {

			for(int seekDist = 0; seekDist < followRange; ++seekDist) {
				for(int xShift = 0;
						xShift <= seekDist;
						xShift = xShift > 0 ? -xShift : 1 - xShift) {

					for(int zShift = xShift < seekDist && xShift > -seekDist ? seekDist : 0;
							zShift <= seekDist;
							zShift = zShift > 0 ? -zShift : 1 - zShift) {

						pos.set(xBase + xShift, yBase + yShift - 1, zBase + zShift);

						if(creature.isWithinRestriction(pos) &&
								targetBlock.test(creature.level().getBlockState(pos))) {
							destinationBlock = pos;
							return true;
						}
					}
				}
			}
		}

		return false;
	}
}
