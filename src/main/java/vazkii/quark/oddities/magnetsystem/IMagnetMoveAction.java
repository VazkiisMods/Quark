package vazkii.quark.oddities.magnetsystem;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMagnetMoveAction {

	void onMoved(World world, BlockPos pos, Direction direction, BlockState state, TileEntity tile);
	
}
