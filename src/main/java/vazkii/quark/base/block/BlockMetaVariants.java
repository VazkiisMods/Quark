/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: http://psi.vazkii.us/license.php
 *
 * File Created @ [31/01/2016, 19:15:27 (GMT)]
 */
package vazkii.quark.base.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockMetaVariants<T extends Enum<T> & IStringSerializable> extends BlockMod {

	public static Class temporaryVariantsEnum; // This is a massive hack, but such is life with constructors
	public static PropertyEnum temporaryVariantProp;

	public final Class<T> variantsEnum;
	public final PropertyEnum<T> variantProp;

	public BlockMetaVariants(String name, Material materialIn, Class<T> variantsEnum) {
		super(name, materialIn, asVariantArray(variantsEnum));

		this.variantsEnum = variantsEnum;
		this.variantProp = temporaryVariantProp;
		setDefaultState(blockState.getBaseState().withProperty(variantProp, variantsEnum.getEnumConstants()[0]));
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, temporaryVariantProp);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.<T>getValue(variantProp == null ? temporaryVariantProp : variantProp).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		if(meta >= variantsEnum.getEnumConstants().length)
			meta = 0;

		return getDefaultState().withProperty(variantProp, variantsEnum.getEnumConstants()[meta]);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(this, 1, getMetaFromState(world.getBlockState(pos)));
	}

	@Override
	public Class<T> getVariantEnum() {
		return variantsEnum;
	}

	@Override
	public IProperty getVariantProp() {
		return variantProp;
	}

	public static String[] asVariantArray(Class e) {
		temporaryVariantsEnum = e;
		temporaryVariantProp = PropertyEnum.create(IVariantEnumHolder.HEADER, e);
		Enum[] values = (Enum[]) e.getEnumConstants();
		String[] variants = new String[values.length];

		for(int i = 0; i < values.length; i++)
			variants[i] = values[i].name().toLowerCase();
		return variants;
	}

	public static interface EnumBase extends IStringSerializable {

		@Override
		public default String getName() {
			return ((Enum) this).name().toLowerCase();
		}

	}

}
