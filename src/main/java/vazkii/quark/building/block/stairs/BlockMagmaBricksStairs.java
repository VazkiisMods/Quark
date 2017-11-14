package vazkii.quark.building.block.stairs;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.building.block.BlockPolishedNetherrack;
import vazkii.quark.building.feature.MagmaBricks;
import vazkii.quark.building.feature.PolishedNetherrack;

public class BlockMagmaBricksStairs extends BlockQuarkStairs {

	public BlockMagmaBricksStairs() {
		super("magma_bricks_stairs", MagmaBricks.magma_bricks.getDefaultState());
		useNeighborBrightness = false;
		setLightLevel(0.2F);
	}
	
	@Override
	public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
		return isSideSolid(world.getBlockState(pos), world, pos, side);
	}
	
    @SideOnly(Side.CLIENT)
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
        return 15728880;
    }

}
