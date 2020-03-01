package vazkii.quark.building.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.Module;
import vazkii.quark.building.module.ThatchModule;

public class ThatchBlock extends QuarkBlock {

	public ThatchBlock(Module module) {
		super("thatch", module, ItemGroup.BUILDING_BLOCKS, 
				Block.Properties.create(Material.PLANTS, MaterialColor.YELLOW)
				.hardnessAndResistance(0.5F)
				.sound(SoundType.PLANT));
	}

	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
		entityIn.onLivingFall(fallDistance, (float) ThatchModule.fallDamageMultiplier);
	}

}
