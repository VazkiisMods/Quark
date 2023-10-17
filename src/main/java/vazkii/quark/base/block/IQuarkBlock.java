package vazkii.quark.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.extensions.IForgeBlock;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * @author WireSegal
 * Created at 1:14 PM on 9/19/19.
 */
public interface IQuarkBlock extends IForgeBlock {

	@Nullable
	QuarkModule getModule();

	IQuarkBlock setCondition(BooleanSupplier condition);

	boolean doesConditionApply();

	default Block getBlock() {
		return (Block) this;
	}

	default boolean isEnabled() {
		QuarkModule module = getModule();
		return module != null && module.enabled && !module.disabledByOverlap && doesConditionApply();
	}

	@Override
	default int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (state.getValues().containsKey(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))
			return 0;

		Material material = state.getMaterial();
		if (material == Material.WOOL || material == Material.LEAVES)
			return 60;
		ResourceLocation loc = Registry.BLOCK.getKey(state.getBlock());
		if (loc != null && (loc.getPath().endsWith("_log") || loc.getPath().endsWith("_wood")) && state.getMaterial().isFlammable())
			return 5;
		return state.getMaterial().isFlammable() ? 20 : 0;
	}

	@Override
	default int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (state.getValues().containsKey(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))
			return 0;

		Material material = state.getMaterial();
		if (material == Material.WOOL || material == Material.LEAVES)
			return 30;
		return state.getMaterial().isFlammable() ? 5 : 0;
	}
	
	static String inheritQuark(IQuarkBlock parent, String format) {
		return inherit(parent.getBlock(), format);
	}
	
	static String inherit(Block parent, String format) {
		ResourceLocation parentName = Quark.ZETA.registry.getRegistryName(parent, Registry.BLOCK);
		return String.format(String.format("%s:%s", Quark.MOD_ID, format), parentName.getPath());
	}
	
	static String inherit(Block parent, Function<String, String> fun) {
		ResourceLocation parentName = Quark.ZETA.registry.getRegistryName(parent, Registry.BLOCK);
		return String.format(String.format("%s:%s", Quark.MOD_ID, fun.apply(parentName.getPath())));
	}
}
