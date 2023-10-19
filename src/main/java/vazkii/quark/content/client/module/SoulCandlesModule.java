package vazkii.quark.content.client.module;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "client")
public class SoulCandlesModule extends QuarkModule {

	private static boolean staticEnabled;
	
	@Hint(key = "soul_fire_candles") TagKey<Item> candles = ItemTags.CANDLES;

	public static ParticleOptions getParticleOptions(ParticleOptions prev, Level level, double x, double y, double z) {
		if(staticEnabled) {
			BlockPos testPos = new BlockPos(x, y - 1, z);
			BlockState testState = level.getBlockState(testPos);
			if (!testState.is(BlockTags.SOUL_FIRE_BASE_BLOCKS) && testState.getEnchantPowerBonus(level, testPos) > 0) {
				testPos = testPos.below();
				testState = level.getBlockState(testPos);
			}
			if(testState.is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
				if(prev == ParticleTypes.SMOKE) {
					if(Math.random() < 0.1)
						return ParticleTypes.SOUL;
				} else if(prev == ParticleTypes.SMALL_FLAME)
					return ParticleTypes.SOUL_FIRE_FLAME;
			}
		}

		return prev;
	}

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		staticEnabled = enabled;
	}

}
