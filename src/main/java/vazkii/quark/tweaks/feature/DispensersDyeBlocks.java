/**
 * This class was created by <ichttt>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [16/03/2017, 16:32:53 (CET)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.block.*;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import vazkii.quark.base.module.Feature;

import javax.annotation.Nonnull;

public class DispensersDyeBlocks extends Feature {
    @Override
    public void init(FMLInitializationEvent event) {
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.DYE, new DyeBehaviour());
    }

    private static class DyeBehaviour extends Bootstrap.BehaviorDispenseOptional {

        @Override
        @Nonnull
        protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            BlockPos pos = source.getBlockPos().offset(source.getBlockState().getValue(BlockDispenser.FACING));
            World world = source.getWorld();
            Block block = world.getBlockState(pos).getBlock();
            EnumDyeColor color = EnumDyeColor.byDyeDamage(stack.getMetadata());
            this.successful = true;

            if (block instanceof BlockShulkerBox) {
                TileEntity tileEntity = world.getTileEntity(pos);

                if (tileEntity instanceof TileEntityShulkerBox) {
                    NBTTagCompound tagCompound = tileEntity.serializeNBT();
                    ((TileEntityShulkerBox) tileEntity).clear();
                    world.setBlockState(pos, BlockShulkerBox.getBlockByColor(color).getDefaultState().withProperty(BlockShulkerBox.FACING, world.getBlockState(pos).getValue(BlockShulkerBox.FACING)));
                    ((TileEntityShulkerBox) world.getTileEntity(pos)).loadFromNbt(tagCompound);
                }

            } else if (block instanceof BlockColored || block instanceof BlockCarpet || block instanceof BlockStainedGlass || block instanceof BlockStainedGlassPane) {
                world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockColored.COLOR, color));

            } else if (color == EnumDyeColor.WHITE) { //Vanilla copy of Bonemeal behaviour
                if (ItemDye.applyBonemeal(stack, world, pos)) {
                    if (!world.isRemote) {
                        world.playEvent(2005, pos, 0);
                    }
                }
                else {
                    this.successful = false;
                }
                return stack;

            } else {
                return super.dispenseStack(source, stack);
            }

            stack.shrink(1);
            return stack;
        }
    }

    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }
}
