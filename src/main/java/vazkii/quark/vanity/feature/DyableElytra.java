/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [21/03/2016, 00:15:14 (GMT)]
 */
package vazkii.quark.vanity.feature;

import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.RecipeSorter;
import vazkii.quark.base.handler.ItemNBTHelper;
import vazkii.quark.base.lib.LibObfuscation;
import vazkii.quark.base.module.Feature;
import vazkii.quark.vanity.client.layer.LayerBetterElytra;
import vazkii.quark.vanity.recipe.ElytraDyingRecipe;

public class DyableElytra extends Feature {

	public static final String TAG_ELYTRA_DYE = "quark:elytraDye";

	@Override
	public void init(FMLInitializationEvent event) {
		GameRegistry.addRecipe(new ElytraDyingRecipe());
		RecipeSorter.register("quark:elytraDying", ElytraDyingRecipe.class, RecipeSorter.Category.SHAPELESS, "");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void postInitClient(FMLPostInitializationEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		RenderManager manager = mc.getRenderManager();
		Map<String, RenderPlayer> renders = manager.getSkinMap();
		for(RenderPlayer render : renders.values())
			messWithRender(render);

		mc.getItemColors().registerItemColorHandler(new IItemColor() {

			@Override
			public int getColorFromItemstack(ItemStack stack, int tintIndex) {
				int color = ItemNBTHelper.getInt(stack, TAG_ELYTRA_DYE, -1);
				if(color == -1 || color == 15)
					return -1;

				return ItemDye.dyeColors[color];
			}

		}, Items.elytra);
	}

	@SideOnly(Side.CLIENT)
	private void messWithRender(RenderPlayer render) {
		List<LayerRenderer> list = ReflectionHelper.getPrivateValue(RenderLivingBase.class, render, LibObfuscation.LAYER_RENDERERS);
		LayerRenderer remove = null;
		for(LayerRenderer layer : list)
			if(layer instanceof LayerElytra) {
				remove = layer;
				break;
			}

		list.remove(remove);
		list.add(new LayerBetterElytra(render));
	}

}
