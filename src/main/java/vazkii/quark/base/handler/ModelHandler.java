/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: http://psi.vazkii.us/license.php
 *
 * File Created @ [08/01/2016, 23:02:00 (GMT)]
 */
package vazkii.quark.base.handler;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameData;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.item.IColorProvider;
import vazkii.quark.base.item.IExtraVariantHolder;
import vazkii.quark.base.item.IVariantHolder;
import vazkii.quark.base.item.ItemMod;
import vazkii.quark.base.lib.LibMisc;

public class ModelHandler {

	public static HashMap<String, ModelResourceLocation> resourceLocations = new HashMap();

	public static void preInit() {
		for(IVariantHolder holder : ItemMod.variantHolders)
			registerModels(holder);
	}

	public static void init() {
		ItemColors colors = Minecraft.getMinecraft().getItemColors();
		for(IVariantHolder holder : ItemMod.variantHolders)
			if(holder instanceof IColorProvider)
				colors.registerItemColorHandler(((IColorProvider) holder).getColor(), (Item) holder);
	}

	public static void registerModels(IVariantHolder holder) {
		ItemMeshDefinition def = holder.getCustomMeshDefinition();
		if(def != null)
			ModelLoader.setCustomMeshDefinition((Item) holder, def);
		else {
			Item i = (Item) holder;
			registerModels(i, holder.getVariants(), false);
			if(holder instanceof IExtraVariantHolder) {
				IExtraVariantHolder extra = (IExtraVariantHolder) holder;
				registerModels(i, extra.getExtraVariants(), true);
			}
		}
	}

	public static void registerModels(Item item, String[] variants, boolean extra) {
		if(item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof IQuarkBlock) {
			IQuarkBlock quarkBlock = (IQuarkBlock) ((ItemBlock) item).getBlock();
			Class clazz = quarkBlock.getVariantEnum();

			IProperty variantProp = quarkBlock.getVariantProp();
			boolean ignoresVariant = false;

			IStateMapper mapper = quarkBlock.getStateMapper();
			IProperty[] ignored = quarkBlock.getIgnoredProperties();
			if(mapper != null || (ignored != null && ignored.length > 0)) {
				if(mapper != null)
					ModelLoader.setCustomStateMapper((Block) quarkBlock, mapper);
				else {
					StateMap.Builder builder = new StateMap.Builder();
					for(IProperty p : ignored) {
						if(p == variantProp)
							ignoresVariant = true;
						builder.ignore(p);
					}

					ModelLoader.setCustomStateMapper((Block) quarkBlock, builder.build());
				}
			}

			if(clazz != null && !ignoresVariant) {
				registerVariantsDefaulted(item, (Block) quarkBlock, clazz, variantProp.getName());
				return;
			}
		}

		for(int i = 0; i < variants.length; i++) {
			String name = LibMisc.PREFIX_MOD + variants[i];
			ModelResourceLocation loc = new ModelResourceLocation(name, "inventory");
			if(!extra) {
				ModelLoader.setCustomModelResourceLocation(item, i, loc);
				resourceLocations.put(getKey(item, i), loc);
			} else {
				ModelBakery.registerItemVariants(item, loc);
				resourceLocations.put(variants[i], loc);
			}
		}
	}

	private static <T extends Enum<T> & IStringSerializable> void registerVariantsDefaulted(Item item, Block b, Class<T> enumclazz, String variantHeader) {
		String baseName = GameData.getBlockRegistry().getNameForObject(b).toString();
		for(T e : enumclazz.getEnumConstants()) {
			String variantName = variantHeader + "=" + e.getName();
			ModelResourceLocation loc = new ModelResourceLocation(baseName, variantName);
			int i = e.ordinal();
			ModelLoader.setCustomModelResourceLocation(item, i, loc);
			resourceLocations.put(getKey(item, i), loc);
		}
	}

	public static ModelResourceLocation getModelLocation(ItemStack stack) {
		if(stack == null)
			return null;

		return getModelLocation(stack.getItem(), stack.getItemDamage());
	}

	public static ModelResourceLocation getModelLocation(Item item, int meta) {
		String key = getKey(item, meta);
		if(resourceLocations.containsKey(key))
			return resourceLocations.get(key);

		return null;
	}

	private static String getKey(Item item, int meta) {
		return "i_" + item.getRegistryName() + "@" + meta;
	}

}
