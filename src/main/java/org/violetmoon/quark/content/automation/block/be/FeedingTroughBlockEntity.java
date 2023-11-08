package org.violetmoon.quark.content.automation.block.be;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.violetmoon.quark.base.handler.MiscUtil;
import org.violetmoon.quark.content.automation.block.FeedingTroughBlock;
import org.violetmoon.quark.content.automation.module.FeedingTroughModule;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author WireSegal
 * Created at 9:39 AM on 9/20/19.
 */
public class FeedingTroughBlockEntity extends RandomizableContainerBlockEntity {

	private static final GameProfile DUMMY_PROFILE = new GameProfile(UUID.randomUUID(), "[FeedingTrough]");

	private NonNullList<ItemStack> stacks;

	private int cooldown = 0;
	private long internalRng = 0;

	public FeedingTroughBlockEntity(BlockPos pos, BlockState state) {
		super(FeedingTroughModule.blockEntityType, pos, state);
		this.stacks = NonNullList.withSize(9, ItemStack.EMPTY);
	}

	public FakePlayer getFoodHolder(TemptGoal goal) {
		FakePlayer foodHolder = null;
		if (level instanceof ServerLevel serverLevel)
			foodHolder = FakePlayerFactory.get(serverLevel, DUMMY_PROFILE);

		Animal entity = (Animal) goal.mob;

		if (foodHolder != null) {
			for (int i = 0; i < getContainerSize(); i++) {
				ItemStack stack = getItem(i);
				if (goal.items.test(stack) && entity.isFood(stack)) {
					Inventory inventory = foodHolder.getInventory();
					inventory.items.set(inventory.selected, stack);
					Vec3 position = new Vec3(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()).add(0.5, -1, 0.5);
					Vec3 direction = goal.mob.position().subtract(position).normalize();
					Vec2 angles = MiscUtil.getMinecraftAngles(direction);

					Vec3 shift = direction.scale(-0.5 / Math.max(
							Math.abs(direction.x), Math.max(
									Math.abs(direction.y),
									Math.abs(direction.z))));

					Vec3 truePos = position.add(shift);

					foodHolder.moveTo(truePos.x, truePos.y, truePos.z, angles.x, angles.y);
					return foodHolder;
				}
			}
		}

		return null;
	}

	public static void tick(Level level, BlockPos pos, BlockState state, FeedingTroughBlockEntity be) {
		if (level != null && !level.isClientSide) {
			if (be.cooldown > 0)
				be.cooldown--;
			else {
				be.cooldown = FeedingTroughModule.cooldown; // minimize aabb calls
				List<Animal> animals = level.getEntitiesOfClass(Animal.class, new AABB(be.worldPosition).inflate(1.5, 0, 1.5).contract(0, 0.75, 0));

				for (Animal creature : animals) {
					if (creature.canFallInLove() && creature.getAge() == 0) {
						for (int i = 0; i < be.getContainerSize(); i++) {
							ItemStack stack = be.getItem(i);
							if (creature.isFood(stack)) {
								SoundEvent soundEvent = creature.getEatingSound(stack);
								if(soundEvent != null)
									creature.playSound(soundEvent, 0.5F + 0.5F * level.random.nextInt(2), (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F);
								
								be.addItemParticles(creature, stack, 16);

								if(be.getSpecialRand().nextDouble() < FeedingTroughModule.loveChance) {
									List<Animal> animalsAround = level.getEntitiesOfClass(Animal.class, new AABB(be.worldPosition).inflate(FeedingTroughModule.range));
									if(animalsAround.size() <= FeedingTroughModule.maxAnimals)
										creature.setInLove(null);
								}

								stack.shrink(1);
								be.setChanged();

								return;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void setChanged() {
		super.setChanged();
		BlockState state = getBlockState();
		if (level != null && state.getBlock() instanceof FeedingTroughBlock) {
			boolean full = state.getValue(FeedingTroughBlock.FULL);
			boolean shouldBeFull = !isEmpty();

			if (full != shouldBeFull)
				level.setBlock(worldPosition, state.setValue(FeedingTroughBlock.FULL, shouldBeFull), 2);
		}
	}

	private void addItemParticles(Entity entity, ItemStack stack, int count) {
		for(int i = 0; i < count; ++i) {
			Vec3 direction = new Vec3((entity.level.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
			direction = direction.xRot(-entity.getXRot() * ((float)Math.PI / 180F));
			direction = direction.yRot(-entity.getYRot() * ((float)Math.PI / 180F));
			double yVelocity = (-entity.level.random.nextFloat()) * 0.6D - 0.3D;
			Vec3 position = new Vec3((entity.level.random.nextFloat() - 0.5D) * 0.3D, yVelocity, 0.6D);
			Vec3 entityPos = entity.position();
			position = position.xRot(-entity.getXRot() * ((float)Math.PI / 180F));
			position = position.yRot(-entity.getYRot() * ((float)Math.PI / 180F));
			position = position.add(entityPos.x, entityPos.y + entity.getEyeHeight(), entityPos.z);
			if (this.level instanceof ServerLevel)
				((ServerLevel)this.level).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), position.x, position.y, position.z, 1, direction.x, direction.y + 0.05D, direction.z, 0.0D);
			else if (this.level != null)
				this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), position.x, position.y, position.z, direction.x, direction.y + 0.05D, direction.z);
		}
	}

	private Random getSpecialRand() {
		Random specialRand = new Random(internalRng);
		internalRng = specialRand.nextLong();
		return specialRand;
	}

	@Override
	public int getContainerSize() {
		return 9;
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < getContainerSize(); i++) {
			ItemStack stack = getItem(i);
			if (!stack.isEmpty())
				return false;
		}

		return true;
	}

	@Override
	@Nonnull
	protected Component getDefaultName() {
		return Component.translatable("quark.container.feeding_trough");
	}

	@Override
	public void load(@Nonnull CompoundTag nbt) {
		super.load(nbt);

		this.cooldown = nbt.getInt("Cooldown");
		this.internalRng = nbt.getLong("rng");
		this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if (!this.tryLoadLootTable(nbt))
			ContainerHelper.loadAllItems(nbt, this.stacks);

	}

	@Override
	protected void saveAdditional(@Nonnull CompoundTag nbt) {
		super.saveAdditional(nbt);

		nbt.putInt("Cooldown", cooldown);
		nbt.putLong("rng", internalRng);
		if (!this.trySaveLootTable(nbt))
			ContainerHelper.saveAllItems(nbt, this.stacks);
	}

	@Override
	@Nonnull
	protected NonNullList<ItemStack> getItems() {
		return this.stacks;
	}

	@Override
	protected void setItems(@Nonnull NonNullList<ItemStack> items) {
		this.stacks = items;
	}

	@Override
	@Nonnull
	protected AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory) {
		return new DispenserMenu(id, playerInventory, this);
	}
}
