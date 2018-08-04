package vazkii.quark.decoration.block;

import java.util.List;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import vazkii.quark.base.block.BlockQuarkPressurePlate;

public class BlockCustomPressurePlate extends BlockQuarkPressurePlate {

	public BlockCustomPressurePlate(String variant) {
		super(variant + "_pressure_plate", Material.WOOD, Sensitivity.EVERYTHING);
		setHardness(0.5F);
		setSoundType(SoundType.WOOD);
	}

	@Override
	protected List<Entity> getValidEntities(World world, AxisAlignedBB aabb) {
		return world.getEntitiesWithinAABBExcludingEntity(null, aabb);
	}

}
