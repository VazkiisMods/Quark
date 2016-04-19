/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: http://psi.vazkii.us/license.php
 *
 * File Created @ [09/01/2016, 22:51:28 (GMT)]
 */
package vazkii.quark.base.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.item.IStateMapperProvider;
import vazkii.quark.base.item.IVariantHolder;

public interface IQuarkBlock extends IVariantHolder, IVariantEnumHolder, IStateMapperProvider {

	public String getBareName();

	public IProperty getVariantProp();

	public IProperty[] getIgnoredProperties();

	public EnumRarity getBlockRarity(ItemStack stack);

	public default boolean shouldDisplayVariant(int variant) {
		return true;
	}
	
	public default IStateMapper getStateMapper() {
		return null;
	}

}
