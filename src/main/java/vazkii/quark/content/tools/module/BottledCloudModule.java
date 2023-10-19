package vazkii.quark.content.tools.module;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.tools.block.CloudBlock;
import vazkii.quark.content.tools.block.be.CloudBlockEntity;
import vazkii.quark.content.tools.client.render.be.CloudRenderer;
import vazkii.quark.content.tools.item.BottledCloudItem;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZClientSetup;

@LoadModule(category = "tools", hasSubscriptions = true)
public class BottledCloudModule extends QuarkModule {

	public static BlockEntityType<CloudBlockEntity> blockEntityType;
	public static Block cloud;
	@Hint public static Item bottled_cloud;
	
	@Config
	public static int cloudLevelBottom = 191;
	
	@Config 
	public static int cloudLevelTop = 196;

	@LoadEvent
	public final void register(ZRegister event) {
		cloud = new CloudBlock(this);
		bottled_cloud = new BottledCloudItem(this);
		
		blockEntityType = BlockEntityType.Builder.of(CloudBlockEntity::new, cloud).build(null);
		Quark.ZETA.registry.register(blockEntityType, "cloud", Registry.BLOCK_ENTITY_TYPE_REGISTRY);
	}
	
	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		BlockEntityRenderers.register(blockEntityType, CloudRenderer::new);
	}
	
	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickItem event) {
		ItemStack stack = event.getItemStack();
		Player player = event.getEntity();
		if(stack.getItem() == Items.GLASS_BOTTLE && player.getY() > cloudLevelBottom && player.getY() < cloudLevelTop) {
			stack.shrink(1);
			
			ItemStack returnStack = new ItemStack(bottled_cloud);
			if(!player.addItem(returnStack))
				player.drop(returnStack, false);
			
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.SUCCESS);
		}
	}
	
}
