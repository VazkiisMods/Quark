/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package vazkii.quark.decoration.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.item.ItemModBlock;
import vazkii.quark.decoration.block.BlockCustomChest;
import vazkii.quark.decoration.feature.CustomChest;
import vazkii.quark.decoration.tileentity.TileEntityCustomChest;

import java.util.List;

public class ItemChestBlock extends ItemModBlock
{
    public ItemChestBlock(Block block) {
        super(block);
    }

    @Override
    public int getMetadata(int damage) {
        return 0;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        int typeCnt = 0;

        BlockPos posN = pos.north();
        BlockPos posS = pos.south();
        BlockPos posW = pos.west();
        BlockPos posE = pos.east();

        BlockCustomChest.CustomChestType myType = CustomChest.custom_chest.getCustomType(stack);

        if( world.getBlockState(posN).getBlock() == this.block && CustomChest.custom_chest.getCustomType(world, posN) == myType ) {
            typeCnt += CustomChest.custom_chest.isDoubleChest(world, posN, myType) ? 2 : 1;
        }
        if( world.getBlockState(posS).getBlock() == this.block && CustomChest.custom_chest.getCustomType(world, posS) == myType ) {
            typeCnt += CustomChest.custom_chest.isDoubleChest(world, posS, myType) ? 2 : 1;
        }
        if( world.getBlockState(posW).getBlock() == this.block && CustomChest.custom_chest.getCustomType(world, posW) == myType ) {
            typeCnt += CustomChest.custom_chest.isDoubleChest(world, posW, myType) ? 2 : 1;
        }
        if( world.getBlockState(posE).getBlock() == this.block && CustomChest.custom_chest.getCustomType(world, posE) == myType ) {
            typeCnt += CustomChest.custom_chest.isDoubleChest(world, posE, myType) ? 2 : 1;
        }

        if( typeCnt <= 1 && super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState) ) {
            TileEntity te = world.getTileEntity(pos);
            if( te instanceof TileEntityCustomChest ) {
                ((TileEntityCustomChest) te).chestType = myType;
                return true;
            }
        }

        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for( BlockCustomChest.CustomChestType type : BlockCustomChest.CustomChestType.VALID_TYPES ) {
            subItems.add(CustomChest.custom_chest.setCustomType(new ItemStack(CustomChest.custom_chest, 1), type));
        }
    }
}
