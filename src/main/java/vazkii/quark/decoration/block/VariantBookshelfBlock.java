package vazkii.quark.decoration.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.Module;

public class VariantBookshelfBlock extends QuarkBlock {

    public VariantBookshelfBlock(String type, Module module) {
        super(type + "_bookshelf", module, ItemGroup.DECORATIONS,
            Block.Properties.from(Blocks.BOOKSHELF));
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
        return 1;
    }
}
