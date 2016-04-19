/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [24/03/2016, 16:54:45 (GMT)]
 */
package vazkii.quark.base.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.item.ItemModBlock;
import vazkii.quark.base.lib.LibMisc;

public class BlockModTrapdoor extends BlockTrapDoor implements IQuarkBlock {

	private final String[] variants;
	private final String bareName;

	public BlockModTrapdoor(String name) {
		super(Material.wood);

		setHardness(3.0F);
		setStepSound(SoundType.WOOD);

		variants = new String[] { name };
		bareName = name;

		setUnlocalizedName(name);
		useNeighborBrightness = true;
	}

	@Override
	public Block setUnlocalizedName(String name) {
		super.setUnlocalizedName(name);
		setRegistryName(LibMisc.PREFIX_MOD + name);
		GameRegistry.register(this);
		GameRegistry.register(new ItemModBlock(this), new ResourceLocation(LibMisc.PREFIX_MOD + name));
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

}
