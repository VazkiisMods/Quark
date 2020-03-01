package vazkii.quark.world.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemGroup;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import vazkii.quark.base.block.QuarkGlassBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.Module;
import vazkii.quark.world.module.underground.CaveCrystalUndergroundBiomeModule;

/**
 * @author WireSegal
 * Created at 12:31 PM on 9/19/19.
 */
public class CaveCrystalBlock extends QuarkGlassBlock {

	private final float[] colorComponents;
	private final Vec3d colorVector;

	public CaveCrystalBlock(String regname, int color, Module module, MaterialColor materialColor) {
		super(regname, module, ItemGroup.DECORATIONS,
				Block.Properties.create(Material.GLASS, materialColor)
				.hardnessAndResistance(0.3F, 0F)
				.sound(SoundType.GLASS)
				.lightValue(11)
				.harvestTool(ToolType.PICKAXE)
				.harvestLevel(0)
				.tickRandomly());

		float r = ((color >> 16) & 0xff) / 255f;
		float g = ((color >> 8) & 0xff) / 255f;
		float b = (color & 0xff) / 255f;
		colorComponents = new float[]{r, g, b};
		colorVector = new Vec3d(r, g, b);
		
		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.TRANSLUCENT);
	}

	private boolean canGrow(World world, BlockPos pos) {
		if(CaveCrystalUndergroundBiomeModule.caveCrystalGrowthChance >= 1 && pos.getY() < 24 && world.isAirBlock(pos.up())) {
			int i;
			for(i = 1; world.getBlockState(pos.down(i)).getBlock() == this; ++i);

			return i < 4;
		}
		return false;
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		if(canGrow(worldIn, pos) && random.nextInt(CaveCrystalUndergroundBiomeModule.caveCrystalGrowthChance) == 0)
			worldIn.setBlockState(pos.up(), state);
	}

	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(canGrow(worldIn, pos)) {
			double d0 = (double)pos.getX() + rand.nextDouble();
			double d1 = (double)pos.getY() + rand.nextDouble();
			double d2 = (double)pos.getZ() + rand.nextDouble();
			worldIn.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, d0, d1, d2, colorComponents[0], colorComponents[1], colorComponents[2]);
		}
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos) {
		return colorComponents;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Vec3d getFogColor(BlockState state, IWorldReader world, BlockPos pos, Entity entity, Vec3d originalColor, float partialTicks) {
		return colorVector;
	}

}
