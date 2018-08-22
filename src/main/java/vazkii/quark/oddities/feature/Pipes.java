package vazkii.quark.oddities.feature;

import net.minecraft.block.Block;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.oddities.block.BlockPipe;
import vazkii.quark.oddities.client.render.RenderTilePipe;
import vazkii.quark.oddities.tile.TilePipe;

public class Pipes extends Feature {

	public static Block pipe;
	
	public static int pipeSpeed;
	public static int maxPipeItems;
	int pipesCrafted;
	boolean enableRender;
	
	@Override
	public void setupConfig() {
		pipeSpeed = loadPropInt("Pipe Speed", "How long it takes for an item to cross a pipe. Bigger = slower.", 5) * 2;
		maxPipeItems = loadPropInt("Max Pipe Items", "Set to 0 if you don't want pipes to have a max amount of items", 16);
		pipesCrafted = loadPropInt("Pipes Crafted", "", 6);
		enableRender = loadPropBool("Enable Pipe Render", "Freel free to disable so you don't see items going through pipes, good if your PC is a potato", true);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		pipe = new BlockPipe();
		registerTile(TilePipe.class, "pipe");
	}
	
	@Override
	public void postPreInit(FMLPreInitializationEvent event) {
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(pipe, pipesCrafted), 
				"IGI",
				'I', "ingotIron",
				'G', "blockGlass");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient(FMLPreInitializationEvent event) {
		if(enableRender)
			ClientRegistry.bindTileEntitySpecialRenderer(TilePipe.class, new RenderTilePipe());
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
