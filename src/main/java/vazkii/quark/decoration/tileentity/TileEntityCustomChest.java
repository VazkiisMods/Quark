/**
 * This class was created by <SanAndreasP>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 22:33:44 (GMT)]
 */
package vazkii.quark.decoration.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import vazkii.quark.decoration.feature.CustomChest;

import javax.annotation.Nullable;

public class TileEntityCustomChest extends TileEntityChest {

    public CustomChest.ChestType chestType = CustomChest.ChestType.NONE;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setString("type", this.chestType.name);
        return nbt;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        nbt.setString("type", this.chestType.name);
        return nbt;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.chestType = CustomChest.ChestType.getType(tag.getString("type"));
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("type", this.chestType.name);
        return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.chestType = CustomChest.ChestType.getType(pkt.getNbtCompound().getString("type"));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.chestType = CustomChest.ChestType.getType(nbt.getString("type"));
    }

    private void setNeighbor(TileEntityChest chestTe, EnumFacing side) {
        if( chestTe.isInvalid() ) {
            this.adjacentChestChecked = false;
        } else if( this.adjacentChestChecked ) {
            switch(side) {
                case NORTH:
                    if(this.adjacentChestZNeg != chestTe)
                        this.adjacentChestChecked = false;
                    break;
                case SOUTH:
                    if(this.adjacentChestZPos != chestTe)
                        this.adjacentChestChecked = false;
                    break;
                case EAST:
                    if(this.adjacentChestXPos != chestTe)
                        this.adjacentChestChecked = false;
                    break;
                case WEST:
                    if(this.adjacentChestXNeg != chestTe)
                        this.adjacentChestChecked = false;
            }
        }
    }

    @Nullable
    @Override
    protected TileEntityChest getAdjacentChest(EnumFacing side) {
        BlockPos blockpos = this.pos.offset(side);

        if(this.isChestAt(blockpos)) {
            TileEntity tileentity = this.worldObj.getTileEntity(blockpos);

            if(tileentity instanceof TileEntityCustomChest) {
                TileEntityCustomChest tileentitychest = (TileEntityCustomChest)tileentity;
                tileentitychest.setNeighbor(this, side.getOpposite());
                return tileentitychest;
            }
        }

        return null;
    }

    private boolean isChestAt(BlockPos posIn) {
        if( this.worldObj == null ) {
            return false;
        } else {
            Block block = this.worldObj.getBlockState(posIn).getBlock();
            TileEntity te = this.worldObj.getTileEntity(posIn);
            return block instanceof BlockChest && ((BlockChest) block).chestType == this.getChestType() && te instanceof TileEntityCustomChest && ((TileEntityCustomChest) te).chestType == chestType;
        }
    }
}
