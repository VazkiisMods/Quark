package org.violetmoon.quark.content.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.quark.content.world.block.be.MonsterBoxBlockEntity;
import org.violetmoon.quark.content.world.module.MonsterBoxModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.RenderLayerRegistry;

import javax.annotation.Nonnull;

public class MonsterBoxBlock extends QuarkBlock implements EntityBlock {

	public MonsterBoxBlock(ZetaModule module) {
		super("monster_box", module, null,
				Block.Properties.of(Material.METAL)
				.strength(25F)
				.sound(SoundType.METAL)
				.noOcclusion());

		module.zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT);
	}

	@Override
	public boolean isPathfindable(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull PathComputationType type) {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
		return new MonsterBoxBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
		return createTickerHelper(type, MonsterBoxModule.blockEntityType, MonsterBoxBlockEntity::tick);
	}

}
