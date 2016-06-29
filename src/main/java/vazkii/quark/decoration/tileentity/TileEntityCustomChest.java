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
import vazkii.quark.decoration.block.BlockCustomChest;

import javax.annotation.Nullable;

public class TileEntityCustomChest
        extends TileEntityChest
{
    public BlockCustomChest.CustomChestType chestType = BlockCustomChest.CustomChestType.NONE;

    public TileEntityCustomChest() {
    }

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
        this.chestType = BlockCustomChest.CustomChestType.getType(tag.getString("type"));
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
        this.chestType = BlockCustomChest.CustomChestType.getType(pkt.getNbtCompound().getString("type"));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        this.chestType = BlockCustomChest.CustomChestType.getType(nbt.getString("type"));
    }

    private void setNeighbor(TileEntityChest chestTe, EnumFacing side) {
        if( chestTe.isInvalid() ) {
            this.adjacentChestChecked = false;
        } else if( this.adjacentChestChecked ) {
            switch(side) {
                case NORTH:
                    if( this.adjacentChestZNeg != chestTe ) {
                        this.adjacentChestChecked = false;
                    }

                    break;
                case SOUTH:
                    if( this.adjacentChestZPos != chestTe ) {
                        this.adjacentChestChecked = false;
                    }

                    break;
                case EAST:
                    if( this.adjacentChestXPos != chestTe ) {
                        this.adjacentChestChecked = false;
                    }

                    break;
                case WEST:
                    if( this.adjacentChestXNeg != chestTe ) {
                        this.adjacentChestChecked = false;
                    }
            }
        }
    }

    @Nullable
    @Override
    protected TileEntityChest getAdjacentChest(EnumFacing side) {
        BlockPos blockpos = this.pos.offset(side);

        if( this.isChestAt(blockpos) ) {
            TileEntity tileentity = this.worldObj.getTileEntity(blockpos);

            if( tileentity instanceof TileEntityCustomChest ) {
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
            return block instanceof BlockChest && ((BlockChest) block).chestType == this.getChestType()
                    && te instanceof TileEntityCustomChest && ((TileEntityCustomChest) te).chestType == chestType;
        }
    }
    //    @Override
//    public Packet getDescriptionPacket() {
//        NBTTagCompound nbt = new NBTTagCompound();
//        nbt.setString("type", this.chestType.name);
//        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
//    }
//
//    @Override
//    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
//        this.chestType = ChestType.getType(pkt.func_148857_g().getString("type"));
//    }

//    @Override
//    public void checkForAdjacentChests() {
//        if( !this.adjacentChestChecked ) {
//            this.adjacentChestChecked = true;
//
//            TileEntity teNegZ = this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);
//            TileEntity tePosZ = this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
//            TileEntity teNegX = this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
//            TileEntity tePosX = this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
//
//            this.adjacentChestZNeg = null;
//            this.adjacentChestZPos = null;
//            this.adjacentChestXNeg = null;
//            this.adjacentChestXPos = null;
//
//            if( this.isSameChest(this.xCoord - 1, this.yCoord, this.zCoord) ) {
//                this.adjacentChestXNeg = (TileEntityCustomChest) teNegX;
//            }
//
//            if( this.isSameChest(this.xCoord + 1, this.yCoord, this.zCoord) ) {
//                this.adjacentChestXPos = (TileEntityCustomChest) tePosX;
//            }
//
//            if( this.isSameChest(this.xCoord, this.yCoord, this.zCoord - 1) ) {
//                this.adjacentChestZNeg = (TileEntityCustomChest) teNegZ;
//            }
//
//            if( this.isSameChest(this.xCoord, this.yCoord, this.zCoord + 1) ) {
//                this.adjacentChestZPos = (TileEntityCustomChest) tePosZ;
//            }
//
//            if( teNegZ instanceof TileEntityCustomChest ) {
//                ((TileEntityCustomChest) teNegZ).updateChestChecked(this, 0);
//            }
//
//            if( tePosZ instanceof TileEntityCustomChest ) {
//                ((TileEntityCustomChest) tePosZ).updateChestChecked(this, 2);
//            }
//
//            if( teNegX instanceof TileEntityCustomChest ) {
//                ((TileEntityCustomChest) teNegX).updateChestChecked(this, 1);
//            }
//
//            if( tePosX instanceof TileEntityCustomChest ) {
//                ((TileEntityCustomChest) tePosX).updateChestChecked(this, 3);
//            }
//        }
//    }
//
//    @Override
//    public int func_145980_j() {
//        return this.chestType.hashCode();
//    }
//
//    public ChestType getChestType() {
//        return this.chestType;
//    }
//
//    @Override
//    public AxisAlignedBB getRenderBoundingBox() {
//        return AxisAlignedBB.getBoundingBox(this.xCoord - 1, this.yCoord, this.zCoord - 1, this.xCoord + 2, this.yCoord + 2, this.zCoord + 2);
//    }
//
//    private boolean isSameChest(int x, int y, int z) {
//        if( this.worldObj == null ) {
//            return false;
//        } else {
//            Block block = this.worldObj.getBlock(x, y, z);
//            return block instanceof BlockCustomChest && block == this.getBlockType() && ((BlockCustomChest) block).getChestType(this.worldObj, x, y, z) == this.chestType;
//        }
//    }
//
//    private void updateChestChecked(TileEntityCustomChest chest, int side) {
//        if( chest.isInvalid() ) {
//            this.adjacentChestChecked = false;
//        } else if( this.adjacentChestChecked ) {
//            switch( side ) {
//                case 0:
//                    if( this.adjacentChestZPos != chest ) {
//                        this.adjacentChestChecked = false;
//                    }
//
//                    break;
//                case 1:
//                    if( this.adjacentChestXNeg != chest ) {
//                        this.adjacentChestChecked = false;
//                    }
//
//                    break;
//                case 2:
//                    if( this.adjacentChestZNeg != chest ) {
//                        this.adjacentChestChecked = false;
//                    }
//
//                    break;
//                case 3:
//                    if( this.adjacentChestXPos != chest ) {
//                        this.adjacentChestChecked = false;
//                    }
//
//                    break;
//            }
//        }
//    }
}
