package vazkii.quark.base.handler;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.block.QuarkSlabBlock;
import vazkii.quark.base.block.QuarkStairsBlock;
import vazkii.quark.base.block.QuarkWallBlock;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class VariantHandler {

	public static final List<QuarkSlabBlock> SLABS = new LinkedList<>();
	public static final List<QuarkStairsBlock> STAIRS = new LinkedList<>();
	public static final List<QuarkWallBlock> WALLS = new LinkedList<>();

	public static Block addSlabStairsWall(IQuarkBlock block) {
		addSlabAndStairs(block);
		addWall(block);
		return block.getBlock();
	}

	public static IQuarkBlock addSlabAndStairs(IQuarkBlock block) {
		addSlab(block);
		addStairs(block);
		return block;
	}

	public static IQuarkBlock addSlab(IQuarkBlock block) {
		SLABS.add(new QuarkSlabBlock(block).setCondition(block::doesConditionApply));
		return block;
	}

	public static IQuarkBlock addStairs(IQuarkBlock block) {
		STAIRS.add(new QuarkStairsBlock(block).setCondition(block::doesConditionApply));
		return block;
	}

	public static IQuarkBlock addWall(IQuarkBlock block) {
		WALLS.add(new QuarkWallBlock(block).setCondition(block::doesConditionApply));
		return block;
	}

	public static FlowerPotBlock addFlowerPot(Block block, String name, Function<Block.Properties, Block.Properties> propertiesFunc) {
		Block.Properties props = Block.Properties.of(Material.DECORATION).strength(0F);
		props = propertiesFunc.apply(props);

		FlowerPotBlock potted = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> block, props);
		RenderLayerHandler.setRenderType(potted, RenderLayerHandler.RenderTypeSkeleton.CUTOUT);
		ResourceLocation resLoc = Quark.ZETA.registry.getRegistryName(block, Registry.BLOCK);
		if (resLoc == null)
			resLoc = new ResourceLocation("missingno");

		Quark.ZETA.registry.registerBlock(potted, "potted_" + name, false);
		((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(resLoc, () -> potted);

		return potted;
	}

	public static BlockBehaviour.Properties realStateCopy(IQuarkBlock parent) {
		BlockBehaviour.Properties props = BlockBehaviour.Properties.copy(parent.getBlock());
		if(parent instanceof IVariantsShouldBeEmissive)
			props = props.emissiveRendering((s, r, p) -> true);

		return props;
	}

	public interface IVariantsShouldBeEmissive {}

}
