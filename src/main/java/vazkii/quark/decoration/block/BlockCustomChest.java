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
package vazkii.quark.decoration.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.item.ItemModBlock;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.decoration.item.ItemChestBlock;
import vazkii.quark.decoration.tileentity.TileEntityCustomChest;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockCustomChest extends BlockChest implements IQuarkBlock {

    private final String[] variants;
    private final String bareName;

    public BlockCustomChest() {
        super(Type.BASIC);

        this.variants = new String[] { "custom_chest" };

        this.bareName = "custom_chest";

        setUnlocalizedName("custom_chest");
    }

    @Override
    public Block setUnlocalizedName(String name) {
        super.setUnlocalizedName(name);
        setRegistryName(LibMisc.PREFIX_MOD + name);
        GameRegistry.register(this);
        GameRegistry.register(new ItemChestBlock(this), new ResourceLocation(LibMisc.PREFIX_MOD + name));

        GameRegistry.registerTileEntity(TileEntityCustomChest.class, LibMisc.PREFIX_MOD + name);

        return this;
    }

    @Override
    public String getBareName() {
        return bareName;
    }

    @Override
    public String[] getVariants() {
        return variants;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemMeshDefinition getCustomMeshDefinition() {
        return null;
    }

    @Override
    public EnumRarity getBlockRarity(ItemStack stack) {
        return EnumRarity.COMMON;
    }

    @Override
    public IProperty[] getIgnoredProperties() {
        return new IProperty[0];
    }

    @Override
    public IProperty getVariantProp() {
        return null;
    }

    @Override
    public Class getVariantEnum() {
        return null;
    }

    public enum CustomChestType {
        NONE(""),
        ACACIA("acacia"),
        BIRCH("birch"),
        DARKOAK("darkoak"),
        JUNGLE("jungle"),
        SPRUCE("spruce");

        public final String name;
        public final ResourceLocation nrmTex;
        public final ResourceLocation dblTex;

        public static final CustomChestType[] VALID_TYPES;
        public static final Map<String, CustomChestType> NAME_TO_TYPE;

        CustomChestType(String name) {
            this.name = name;
            this.nrmTex = new ResourceLocation(LibMisc.PREFIX_MOD + "textures/blocks/chest/" + name + ".png");
            this.dblTex = new ResourceLocation(LibMisc.PREFIX_MOD + "textures/blocks/chest/" + name + "_double.png");
        }

        public static CustomChestType getType(String type) {
            return NAME_TO_TYPE.containsKey(type) ? NAME_TO_TYPE.get(type) : NONE;
        }

        static {
            VALID_TYPES = new CustomChestType[] {ACACIA, BIRCH, DARKOAK, JUNGLE, SPRUCE};
            NAME_TO_TYPE = new HashMap<>();
            for( CustomChestType type : VALID_TYPES ) {
                NAME_TO_TYPE.put(type.name, type);
            }
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        CustomChestType myType = getCustomType(source, pos);
        return getCustomType(source, pos.north()) == myType ? NORTH_CHEST_AABB : (getCustomType(source, pos.south()) == myType ? SOUTH_CHEST_AABB : (getCustomType(source, pos.west()) == myType ? WEST_CHEST_AABB : (getCustomType(source, pos.east()) == myType ? EAST_CHEST_AABB : NOT_CONNECTED_AABB)));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing facing = EnumFacing.getHorizontal(MathHelper.floor_double((placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3).getOpposite();
        state = state.withProperty(FACING, facing);
        BlockPos northPos = pos.north();
        BlockPos southPos = pos.south();
        BlockPos westPos = pos.west();
        BlockPos eastPos = pos.east();

        CustomChestType myType = getCustomType(stack);

        boolean northChest = myType == getCustomType(worldIn, northPos);
        boolean southChest = myType == getCustomType(worldIn, southPos);
        boolean westChest = myType == getCustomType(worldIn, westPos);
        boolean eastChest = myType == getCustomType(worldIn, eastPos);

        if( !northChest && !southChest && !westChest && !eastChest ) {
            worldIn.setBlockState(pos, state, 3);
        } else if( facing.getAxis() != EnumFacing.Axis.X || !northChest && !southChest ) {
            if( facing.getAxis() == EnumFacing.Axis.Z && (westChest || eastChest) ) {
                if( westChest ) {
                    worldIn.setBlockState(westPos, state, 3);
                } else {
                    worldIn.setBlockState(eastPos, state, 3);
                }

                worldIn.setBlockState(pos, state, 3);
            }
        } else {
            if( northChest ) {
                worldIn.setBlockState(northPos, state, 3);
            } else {
                worldIn.setBlockState(southPos, state, 3);
            }

            worldIn.setBlockState(pos, state, 3);
        }

        TileEntity te = worldIn.getTileEntity(pos);
        if( te instanceof TileEntityCustomChest ) {
            TileEntityCustomChest chest = (TileEntityCustomChest) te;
            if( stack.hasDisplayName() ) {
                chest.setCustomName(stack.getDisplayName());
            }

            chest.chestType = myType;
        }

        this.onBlockAdded(worldIn, pos, state);
    }

    @Override
    @Deprecated
    public IBlockState checkForSurroundingChests(World worldIn, BlockPos pos, IBlockState state) {
        return state;
    }

    public IBlockState checkForSurroundingChests(World worldIn, BlockPos pos, IBlockState state, CustomChestType myType) {
        if( worldIn.isRemote ) {
            return state;
        } else {
            IBlockState stateN = worldIn.getBlockState(pos.north());
            IBlockState stateS = worldIn.getBlockState(pos.south());
            IBlockState stateW = worldIn.getBlockState(pos.west());
            IBlockState stateE = worldIn.getBlockState(pos.east());

            CustomChestType typeN = getCustomType(worldIn, pos.north());
            CustomChestType typeS = getCustomType(worldIn, pos.south());
            CustomChestType typeW = getCustomType(worldIn, pos.west());
            CustomChestType typeE = getCustomType(worldIn, pos.east());

            EnumFacing facing = state.getValue(FACING);

            if( typeN != myType && typeS != myType ) {
                boolean fullBlockN = stateN.isFullBlock();
                boolean fullBlockS = stateS.isFullBlock();

                if( typeW == myType || typeE == myType ) {
                    BlockPos adjPos = typeW == myType ? pos.west() : pos.east();
                    IBlockState adjStateN = worldIn.getBlockState(adjPos.north());
                    IBlockState adjStateS = worldIn.getBlockState(adjPos.south());
                    facing = EnumFacing.SOUTH;
                    EnumFacing facingAdj;

                    if( typeW == myType ) {
                        facingAdj = stateW.getValue(FACING);
                    } else {
                        facingAdj = stateE.getValue(FACING);
                    }

                    if( facingAdj == EnumFacing.NORTH ) {
                        facing = EnumFacing.NORTH;
                    }

                    if( (fullBlockN || adjStateN.isFullBlock()) && !fullBlockS && !adjStateS.isFullBlock() ) {
                        facing = EnumFacing.SOUTH;
                    }

                    if ((fullBlockS || adjStateS.isFullBlock()) && !fullBlockN && !adjStateN.isFullBlock()) {
                        facing = EnumFacing.NORTH;
                    }
                }
            } else {
                BlockPos adjPos = typeN == myType ? pos.north() : pos.south();
                IBlockState adjStateW = worldIn.getBlockState(adjPos.west());
                IBlockState adjStateE = worldIn.getBlockState(adjPos.east());
                facing = EnumFacing.EAST;
                EnumFacing facingAdj;

                if( typeN == myType ) {
                    facingAdj = stateN.getValue(FACING);
                } else {
                    facingAdj = stateS.getValue(FACING);
                }

                if( facingAdj == EnumFacing.WEST ) {
                    facing = EnumFacing.WEST;
                }

                if( (stateW.isFullBlock() || adjStateW.isFullBlock()) && !stateE.isFullBlock() && !adjStateE.isFullBlock() ) {
                    facing = EnumFacing.EAST;
                }

                if( (stateE.isFullBlock() || adjStateE.isFullBlock()) && !stateW.isFullBlock() && !adjStateW.isFullBlock() ) {
                    facing = EnumFacing.WEST;
                }
            }

            state = state.withProperty(FACING, facing);
            worldIn.setBlockState(pos, state, 3);
            return state;
        }
    }

    @Override
    @Deprecated
    public IBlockState correctFacing(World worldIn, BlockPos pos, IBlockState state) {
        return correctFacing(worldIn, pos, state, CustomChestType.NONE);
    }

    public IBlockState correctFacing(World worldIn, BlockPos pos, IBlockState state, CustomChestType myType) {
        EnumFacing facing = null;

        for( EnumFacing horizFace : EnumFacing.Plane.HORIZONTAL ) {
            if( getCustomType(worldIn, pos.offset(horizFace)) == myType ) {
                return state;
            }

            if( worldIn.getBlockState(pos.offset(horizFace)).isFullBlock() ) {
                if( facing != null ) {
                    facing = null;
                    break;
                }

                facing = horizFace;
            }
        }

        if( facing != null ) {
            return state.withProperty(FACING, facing.getOpposite());
        } else {
            EnumFacing enumfacing2 = state.getValue(FACING);

            if( worldIn.getBlockState(pos.offset(enumfacing2)).isFullBlock() ) {
                enumfacing2 = enumfacing2.getOpposite();
            }

            if( worldIn.getBlockState(pos.offset(enumfacing2)).isFullBlock() ) {
                enumfacing2 = enumfacing2.rotateY();
            }

            if( worldIn.getBlockState(pos.offset(enumfacing2)).isFullBlock() ) {
                enumfacing2 = enumfacing2.getOpposite();
            }

            return state.withProperty(FACING, enumfacing2);
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return true;
    }

    public boolean isDoubleChest(World worldIn, BlockPos pos, CustomChestType myType) {
        if( getCustomType(worldIn, pos) != myType ) {
            return false;
        } else {
            CustomChestType theType = getCustomType(worldIn, pos);
            for( EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL ) {
                if( getCustomType(worldIn, pos.offset(enumfacing)) == theType ) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, false);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if( te instanceof TileEntityCustomChest ) {
            te.invalidate();
        }
        worldIn.setBlockToAir(pos);
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCustomChest();
    }

    public CustomChestType getCustomType(IBlockAccess source, BlockPos pos) {
        if( source.getBlockState(pos).getBlock() == this ) {
            TileEntity te = source.getTileEntity(pos);
            if( te instanceof TileEntityCustomChest ) {
                return ((TileEntityCustomChest) te).chestType;
            }
        }

        return CustomChestType.NONE;
    }

    public CustomChestType getCustomType(ItemStack stack) {
        if( stack.hasTagCompound() ) {
            return CustomChestType.getType(stack.getTagCompound().getString("customType"));
        }

        return CustomChestType.NONE;
    }

    public ItemStack setCustomType(ItemStack stack, CustomChestType type) {
        NBTTagCompound nbt = stack.getTagCompound();
        if( nbt == null ) {
            nbt = new NBTTagCompound();
        }

        nbt.setString("customType", type.name);
        stack.setTagCompound(nbt);

        return stack;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return new ArrayList<>(Collections.singletonList(setCustomType(new ItemStack(this, 1), getCustomType(world, pos))));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return setCustomType(new ItemStack(this, 1), getCustomType(world, pos));
    }
}
