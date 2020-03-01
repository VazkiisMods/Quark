package vazkii.quark.building.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.Module;

public class MagmaBrickBlock extends QuarkBlock {

	public MagmaBrickBlock(Module module) {
		super("magma_bricks", module, ItemGroup.BUILDING_BLOCKS, 
				Block.Properties.from(Blocks.MAGMA_BLOCK)
				.hardnessAndResistance(1.5F, 10F));
	}
	
	@Override
	public boolean isFireSource(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return true;
	}
	
	@Override
	public boolean isEmissiveRendering(BlockState p_225543_1_) {
		return true;
	}
	
	@Override
	public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
		return type.isImmuneToFire();
	}

}
