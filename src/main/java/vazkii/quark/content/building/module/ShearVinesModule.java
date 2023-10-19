package vazkii.quark.content.building.module;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.building.block.CutVineBlock;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "building", hasSubscriptions = true)
public class ShearVinesModule extends QuarkModule {

	public static Block cut_vine;

	@Hint Item vine = Items.VINE;
	
	@LoadEvent
	public final void register(ZRegister event) {
		cut_vine = new CutVineBlock(this);
	}
	
	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		ItemStack stack = event.getItemStack();
		if(stack.canPerformAction(ToolActions.SHEARS_CARVE)) {
			BlockPos pos = event.getPos();
			Level world = event.getLevel();
			BlockState state = world.getBlockState(pos);
			
			if(state.getBlock() == Blocks.VINE) {
				BlockState newState = cut_vine.defaultBlockState();
				Map<Direction, BooleanProperty> map = VineBlock.PROPERTY_BY_DIRECTION;
				for(Direction d : map.keySet()) {
					BooleanProperty prop = map.get(d);
					newState = newState.setValue(prop, state.getValue(prop));
				}
				
				world.setBlockAndUpdate(pos, newState);
				
				BlockPos testPos = pos.below();
				BlockState testState = world.getBlockState(testPos);
				while(testState.is(Blocks.VINE) || testState.is(cut_vine)) {
					world.removeBlock(testPos, false);
					testPos = testPos.below();
					testState = world.getBlockState(testPos);
				}
				
				Player player = event.getEntity();
				world.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 0.5F, 1F);
				if(!player.getAbilities().instabuild)
					MiscUtil.damageStack(player, event.getHand(), stack, 1);
				
				event.setCancellationResult(InteractionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}
	
}
