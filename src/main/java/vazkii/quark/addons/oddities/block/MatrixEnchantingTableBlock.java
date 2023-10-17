package vazkii.quark.addons.oddities.block;

import java.util.function.BooleanSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import vazkii.quark.addons.oddities.block.be.MatrixEnchantingTableBlockEntity;
import vazkii.quark.addons.oddities.module.MatrixEnchantingModule;
import vazkii.quark.api.IEnchantmentInfluencer;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;

public class MatrixEnchantingTableBlock extends EnchantmentTableBlock implements IQuarkBlock {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public MatrixEnchantingTableBlock(QuarkModule module) {
		super(Block.Properties.copy(Blocks.ENCHANTING_TABLE));

		this.module = module;
		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_DECORATIONS);
		Quark.ZETA.registry.registerBlock(this, "matrix_enchanter", true);
	}

	@Nonnull
	@Override
	public MutableComponent getName() {
		return Blocks.ENCHANTING_TABLE.getName();
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public MatrixEnchantingTableBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
		return new MatrixEnchantingTableBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
		return createTickerHelper(type, MatrixEnchantingModule.blockEntityType, MatrixEnchantingTableBlockEntity::tick);
	}

	@Nonnull
	@Override
	public InteractionResult use(@Nonnull BlockState state, Level worldIn, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand handIn, @Nonnull BlockHitResult raytrace) {
		if(!(worldIn.getBlockEntity(pos) instanceof MatrixEnchantingTableBlockEntity))
			worldIn.setBlockEntity(newBlockEntity(pos, state));

		if(ModuleLoader.INSTANCE.isModuleEnabled(MatrixEnchantingModule.class)) {
			if(player instanceof ServerPlayer serverPlayer)
				NetworkHooks.openScreen(serverPlayer, (MatrixEnchantingTableBlockEntity) worldIn.getBlockEntity(pos), pos);
		} else
			worldIn.setBlockAndUpdate(pos, Blocks.ENCHANTING_TABLE.defaultBlockState());

		return InteractionResult.SUCCESS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(@Nonnull BlockState stateIn, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
		boolean enabled = ModuleLoader.INSTANCE.isModuleEnabled(MatrixEnchantingModule.class);
		boolean showInfluences = enabled && MatrixEnchantingModule.allowInfluencing;
		boolean allowUnderwater = enabled && MatrixEnchantingModule.allowUnderwaterEnchanting;
		boolean allowShort = enabled && MatrixEnchantingModule.allowShortBlockEnchanting;

		for(int i = -2; i <= 2; ++i)
			for(int j = -2; j <= 2; ++j) {
				if(i > -2 && i < 2 && j == -1)
					j = 2;

				if(rand.nextInt(16) == 0)
					for(int k = 0; k <= 1; ++k) {
						BlockPos blockpos = pos.offset(i, k, j);
						BlockState state = worldIn.getBlockState(blockpos);
						BlockPos test = pos.offset(i / 2, 0, j / 2);
						if(!(worldIn.isEmptyBlock(test) 
								|| (allowUnderwater && worldIn.getBlockState(test).getBlock() == Blocks.WATER)
								|| (allowShort && MatrixEnchantingTableBlockEntity.isShortBlock(worldIn, test))))
							break;

						if(showInfluences) {
							IEnchantmentInfluencer influencer = MatrixEnchantingTableBlockEntity.getInfluencerFromBlock(state, worldIn, blockpos);

							if (influencer != null) {
								float[] comp = influencer.getEnchantmentInfluenceColor(worldIn, blockpos, state);
								ParticleOptions extra = influencer.getExtraParticleOptions(worldIn, blockpos, state);
								double chance = influencer.getExtraParticleChance(worldIn, blockpos, state);

								if (comp != null || extra != null) {
									int steps = 20;
									double dx = (double) (pos.getX() - blockpos.getX()) / steps;
									double dy = (double) (pos.getY() - blockpos.getY()) / steps;
									double dz = (double) (pos.getZ() - blockpos.getZ()) / steps;

									for (int p = 0; p < steps; p++) {
										boolean doDust = comp != null && rand.nextDouble() < 0.5;
										boolean doExtra = extra != null && rand.nextDouble() < chance;
										if (!doDust && !doExtra)
											continue;

										double px = blockpos.getX() + 0.5 + dx * p + rand.nextDouble() * 0.2 - 0.1;
										double py = blockpos.getY() + 0.5 + dy * p + Math.sin((double) p / steps * Math.PI) * 0.5 + rand.nextDouble() * 0.2 - 0.1;
										double pz = blockpos.getZ() + 0.5 + dz * p + rand.nextDouble() * 0.2 - 0.1;

										if (doDust)
											worldIn.addParticle(new DustParticleOptions(new Vector3f(comp[0], comp[1], comp[2]), 1F), px, py, pz, 0, 0, 0);
										if (doExtra)
											worldIn.addParticle(extra, px, py, pz, 0, 0, 0);
									}
								}
							}
						}

						if(state.getEnchantPowerBonus(worldIn, blockpos) > 0) {
							worldIn.addParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 2.0, pos.getZ() + 0.5, i + rand.nextFloat() - 0.5, k - rand.nextFloat() - 1.0, j + rand.nextFloat() - 0.5);
						}
					}
			}
	}

	@Override
	public void setPlacedBy(@Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull LivingEntity placer, @Nonnull ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);

		if(stack.hasCustomHoverName()) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);

			if(tileentity instanceof MatrixEnchantingTableBlockEntity matrixEnchanter)
				matrixEnchanter.setCustomName(stack.getHoverName());
		}
	}

	@Override
	public void onRemove(@Nonnull BlockState state, Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);

		if(tileentity instanceof MatrixEnchantingTableBlockEntity enchanter) {
			enchanter.dropItem(0);
			enchanter.dropItem(1);
		}

		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

}
