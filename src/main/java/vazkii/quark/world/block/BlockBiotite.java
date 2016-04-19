/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [18/04/2016, 17:45:33 (GMT)]
 */
package vazkii.quark.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.item.ItemModBlock;
import vazkii.quark.base.lib.LibMisc;

public class BlockBiotite extends BlockQuartz implements IQuarkBlock {

	private final String[] variants;
	private final String bareName;

	public BlockBiotite() {
		setStepSound(SoundType.STONE);
		setHardness(0.8F);

		String name = "biotite_block";
		variants = new String[] { name, "chiseled_biotite_block", "pillar_biotite_block" };
		bareName = name;

		setUnlocalizedName(name);
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
	
	@Override
	public IStateMapper getStateMapper() {
		return new StateMapperBase() {
			
			@Override
            public ModelResourceLocation getModelResourceLocation(IBlockState state) {
                BlockQuartz.EnumType blockquartz$enumtype = (BlockQuartz.EnumType)state.getValue(BlockQuartz.VARIANT);
                ResourceLocation baseLocation = new ResourceLocation(LibMisc.MOD_ID.toLowerCase(), "biotite_block");
                
                switch (blockquartz$enumtype)  {
                    case CHISELED: return new ModelResourceLocation(baseLocation, "chiseled");
                    case LINES_Y: return new ModelResourceLocation(baseLocation, "axis=y");
                    case LINES_X: return new ModelResourceLocation(baseLocation, "axis=x");
                    case LINES_Z: return new ModelResourceLocation(baseLocation, "axis=z");
                    default: return new ModelResourceLocation(baseLocation, "normal");
                }
            }
        };
	}

}
