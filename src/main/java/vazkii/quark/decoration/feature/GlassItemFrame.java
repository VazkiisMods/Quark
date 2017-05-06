package vazkii.quark.decoration.feature;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.client.render.RenderGlassItemFrame;
import vazkii.quark.decoration.entity.EntityGlassItemFrame;
import vazkii.quark.decoration.item.ItemGlassItemFrame;

public class GlassItemFrame extends Feature {
	
	public static Item glass_item_frame;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		glass_item_frame = new ItemGlassItemFrame();

		String glassItemFrameName = "quark:glass_item_frame";
		EntityRegistry.registerModEntity(new ResourceLocation(glassItemFrameName), EntityGlassItemFrame.class, glassItemFrameName, LibEntityIDs.GLASS_ITEM_FRAME, Quark.instance, 256, 64, false);

		RecipeHandler.addOreDictRecipe(new ItemStack(glass_item_frame), 
				"GGG", "GFG", "GGG",
				'G', new ItemStack(Blocks.GLASS_PANE),
				'F', new ItemStack(Items.ITEM_FRAME));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityGlassItemFrame.class, RenderGlassItemFrame.FACTORY);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
