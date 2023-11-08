package org.violetmoon.quark.content.tools.entity.rang;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener.VibrationListenerConfig;
import net.minecraft.world.phys.Vec3;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.tools.config.PickarangType;
import org.violetmoon.quark.content.tools.module.PickarangModule;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

import static org.violetmoon.quark.content.tools.module.PickarangModule.echorangBreaksAnywayTag;

public class Echorang extends AbstractPickarang<Echorang> implements VibrationListenerConfig {

	private final DynamicGameEventListener<VibrationListener> dynamicGameEventListener;

	public Echorang(EntityType<Echorang> type, Level worldIn) {
		super(type, worldIn);
		dynamicGameEventListener = makeListener();
	}

	public Echorang(EntityType<Echorang> type, Level worldIn, Player thrower) {
		super(type, worldIn, thrower);
		dynamicGameEventListener = makeListener();
	}

	private DynamicGameEventListener<VibrationListener> makeListener() {
		return new DynamicGameEventListener<>(new VibrationListener(new EntityPositionSource(this, this.getEyeHeight()), 16, this, (VibrationListener.ReceivingEvent) null, 0.0F, 0));
	}

	@Override
	protected void emitParticles(Vec3 pos, Vec3 ourMotion) {
		if(Math.random() < 0.4)
			this.level.addParticle(ParticleTypes.SCULK_SOUL,
					pos.x - ourMotion.x * 0.25D + (Math.random() - 0.5) * 0.4,
					pos.y - ourMotion.y * 0.25D + (Math.random() - 0.5) * 0.4,
					pos.z - ourMotion.z * 0.25D + (Math.random() - 0.5) * 0.4,
					(Math.random() - 0.5) * 0.1,
					(Math.random() - 0.5) * 0.1,
					(Math.random() - 0.5) * 0.1);
	}

	@Override
	protected boolean canDestroyBlock(BlockState state) {
		return super.canDestroyBlock(state) || state.is(echorangBreaksAnywayTag);
	}

	@Override
	public boolean hasDrag() {
		return false;
	}

	@Override
	public TagKey<GameEvent> getListenableEvents() {
		return PickarangModule.echorangCanListenTag;
	}

	@Override
	public boolean isValidVibration(GameEvent p_223878_, Context p_223879_) {
		return p_223878_.is(getListenableEvents()) && p_223879_.sourceEntity() == getOwner();
	}

	@Override
	public boolean shouldListen(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, Context context) {
		return !isReturning() && level.getWorldBorder().isWithinBounds(pos) && !isRemoved() && this.level == level;
	}

	@Override
	public void onSignalReceive(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity receiving, @Nullable Entity projectileOwner, float distance) {
		liveTime = 0;
	}

	@Override
	public void tick() {
		super.tick();

		gameEvent(GameEvent.PROJECTILE_SHOOT);

		if(level instanceof ServerLevel serverlevel)
			this.dynamicGameEventListener.getListener().tick(serverlevel);
	}

	@Override
	public PickarangType<Echorang> getPickarangType() {
		return PickarangModule.echorangType;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);

		VibrationListener.codec(this).encodeStart(NbtOps.INSTANCE, dynamicGameEventListener.getListener()).resultOrPartial(Quark.LOG::error).ifPresent((nbt) -> {
			compound.put("listener", nbt);
		});
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);

		if(compound.contains("listener", 10))
			VibrationListener.codec(this).parse(new Dynamic<>(NbtOps.INSTANCE, compound.getCompound("listener"))).resultOrPartial(Quark.LOG::error).ifPresent((nbt) -> {
				dynamicGameEventListener.updateListener(nbt, level);
			});
	}

	@Override
	public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> consumer) {
		if(level instanceof ServerLevel serverlevel)
			consumer.accept(this.dynamicGameEventListener, serverlevel);
	}

}
