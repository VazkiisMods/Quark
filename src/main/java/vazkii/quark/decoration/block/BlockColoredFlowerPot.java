package vazkii.quark.decoration.block;

import java.util.LinkedHashMap;
import java.util.Random;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.arl.interf.IRecipeGrouped;
import vazkii.arl.item.ItemModBlock;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.lib.LibMisc;

public class BlockColoredFlowerPot extends BlockCustomFlowerPot implements IQuarkBlock, IBlockColorProvider, IRecipeGrouped {

	private final String[] variants;
	private final String bareName;

	public BlockColoredFlowerPot(EnumDyeColor color) {
		String name = "colored_flowerpot_" + color.getName();
		variants = new String[] { name };
		bareName = name;

		setCreativeTab(CreativeTabs.DECORATIONS);
		setUnlocalizedName(name);
	}

	@Override
	public Block setUnlocalizedName(String name) {
		super.setUnlocalizedName(name);
		setRegistryName(LibMisc.PREFIX_MOD + name);
		ProxyRegistry.register(this);
		ProxyRegistry.register(new ItemModBlock(this, new ResourceLocation(LibMisc.PREFIX_MOD + name)));
		return this;
	}
	
	@Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        ItemStack stack = super.getItem(worldIn, pos, state);
        if(stack.getItem() == Items.FLOWER_POT)
        	stack = new ItemStack(Item.getItemFromBlock(this));
        
        return stack;
    }

	@Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}
	
	@Override
    public String getLocalizedName() {
        return I18n.translateToLocal(getUnlocalizedName() + ".name");
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
		return null;
	}

	@Override
	public IStateMapper getStateMapper() {
		return ColoredFlowerPotStateMapper.INSTANCE;
	}

	@Override
	public IProperty getVariantProp() {
		return null;
	}

	@Override
	public Class getVariantEnum() {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IItemColor getItemColor() {
		return (stack, i) -> 0xFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockColor getBlockColor() {
		return (state, world, pos, i) -> Minecraft.getMinecraft().getBlockColors().colorMultiplier(Blocks.FLOWER_POT.getDefaultState(), world, pos, i);
	}
	
	@Override
	public String getRecipeGroup() {
		return "colored_flower_pot";
	}

	/**
	 * Remaps the custom flag to be part of the contents enum, to prevent needing twice as many variants
	 * Mainly used since Forge blockstates do not handle the model being set by two different properties well
	 */
	private static class ColoredFlowerPotStateMapper extends StateMapperBase {
		public static final ColoredFlowerPotStateMapper INSTANCE = new ColoredFlowerPotStateMapper();

		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			ResourceLocation loc = state.getBlock().getRegistryName();
			if(state.getValue(CUSTOM)) {
				return new ModelResourceLocation(loc, "contents=custom");
			}

			LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
			map.remove(CUSTOM);
			map.remove(LEGACY_DATA);

			return new ModelResourceLocation(loc, this.getPropertyString(map));
		}
	}
}
