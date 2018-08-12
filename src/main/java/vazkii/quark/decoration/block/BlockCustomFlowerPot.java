package vazkii.quark.decoration.block;

import java.util.LinkedHashMap;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.arl.interf.IStateMapperProvider;
import vazkii.quark.base.property.PropertyString;
import vazkii.quark.decoration.feature.ColoredFlowerPots;

public class BlockCustomFlowerPot extends BlockFlowerPot implements IBlockColorProvider, IStateMapperProvider {

	public static final PropertyBool CUSTOM = PropertyBool.create("custom");
	// TODO: move the property to a common place if using this model for another block
	public static final PropertyString TEXTURE = new PropertyString("texture");
	public static final String TAG_TEXTURE_PATH = "texture_path";

	public BlockCustomFlowerPot() {
		this.setHardness(0.0F);
		this.setSoundType(SoundType.STONE);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(CONTENTS, EnumFlowerType.EMPTY)
				.withProperty(LEGACY_DATA, 0)
				.withProperty(CUSTOM, false));
	}

	@Override
	protected ExtendedBlockState createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] {CONTENTS, LEGACY_DATA, CUSTOM}, new IUnlistedProperty[] {TEXTURE});
	}

	/**
	 * Called when the block is right clicked by a player.
	 */
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		// this is basically a copy of the original method since I needed to override the private method canBePotted
		ItemStack stack = player.getHeldItem(hand);
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityFlowerPot)) {
			return false;
		}
		TileEntityFlowerPot flowerPot = (TileEntityFlowerPot) te;
		ItemStack flower = flowerPot.getFlowerItemStack();

		if(flower.isEmpty()) {
			if(!ColoredFlowerPots.isFlower(stack)) {
				return false;
			}

			flowerPot.setItemStack(stack);
			flowerPot.getTileData().removeTag(TAG_TEXTURE_PATH);
			player.addStat(StatList.FLOWER_POTTED);

			if(!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
		} else {
			ItemHandlerHelper.giveItemToPlayer(player, flower, player.inventory.currentItem);
			flowerPot.setItemStack(ItemStack.EMPTY);
		}

		flowerPot.markDirty();
		world.notifyBlockUpdate(pos, state, state, 3);
		return true;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		state = super.getActualState(state, world, pos);
		// if the flower pot type is empty, but we have a flower, set the extra flag
		if(state.getValue(CONTENTS) == EnumFlowerType.EMPTY) {
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileEntityFlowerPot) {
				if(!((TileEntityFlowerPot)te).getFlowerItemStack().isEmpty()) {
					state = state.withProperty(CUSTOM, true);
				}
			}
		}

		return state;
	}

	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
		if(!state.getValue(CUSTOM)) {
			return state;
		}

		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityFlowerPot) {
			TileEntityFlowerPot flowerPot = (TileEntityFlowerPot) te;

			// try loading the texture from the client TE
			String texture = flowerPot.getTileData().getString(TAG_TEXTURE_PATH);
			if(texture.isEmpty()) {
				// if missing load it from stored block
				ItemStack stack = flowerPot.getFlowerItemStack();
				if(!stack.isEmpty()) {
					Block block = Block.getBlockFromItem(stack.getItem());
					if(block != Blocks.AIR) {
						// logic to obtain texture string
					    IBlockState blockState = block.getStateFromMeta(stack.getItem().getMetadata(stack));
					    texture = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockState).getIconName();
						flowerPot.getTileData().setString(TAG_TEXTURE_PATH, texture);
					}
				}
			}
			if(!texture.isEmpty()) {
				state = ((IExtendedBlockState)state).withProperty(TEXTURE, texture);
			}
		}

		return state;
	}


	/*
	 * Comparator
	 */

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return ColoredFlowerPots.enableComparatorLogic;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
		if(!ColoredFlowerPots.enableComparatorLogic) {
			return 0;
		}
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityFlowerPot) {
			return getComparatorSignal(((TileEntityFlowerPot) te).getFlowerItemStack());
		}

		return 0;
	}

	private int getComparatorSignal(ItemStack stack) {
		if(stack.isEmpty()) {
			return 0;
		}

		return ColoredFlowerPots.getFlowerComparatorPower(stack);
	}

	@Override
	public IItemColor getItemColor() {
		return (stack, i) -> -1;
	}

	@Override
	public IBlockColor getBlockColor() {
		return (state, world, pos, index) -> {
			if (world != null && pos != null) {
				TileEntity tileentity = world.getTileEntity(pos);
				if (tileentity instanceof TileEntityFlowerPot) {
					ItemStack stack = ((TileEntityFlowerPot)tileentity).getFlowerItemStack();
					return ColoredFlowerPots.getStackBlockColorsSafe(stack, world, pos, 0);
				}
			}
			return -1;
		};
	}

	@Override
	public IStateMapper getStateMapper() {
		return FlowerPotStateMapper.INSTANCE;
	}

	/**
	 * Remaps the custom flag to be a separate model file. Used for compatibility with vanilla resource packs over replacing the resource
	 */
	public static class FlowerPotStateMapper extends StateMapperBase {
		public static final FlowerPotStateMapper INSTANCE = new FlowerPotStateMapper();
		public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation("quark", "custom_flower_pot"), "normal");

		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			if(state.getValue(CUSTOM)) {
				return LOCATION;
			}

			LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
			map.remove(CUSTOM);
			map.remove(LEGACY_DATA);

			return new ModelResourceLocation(state.getBlock().getRegistryName(), this.getPropertyString(map));
		}
	}
}