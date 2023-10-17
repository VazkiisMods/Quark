package vazkii.quark.integration.lootr;

import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.ModList;
import vazkii.zeta.event.client.ZPreTextureStitch;

import javax.annotation.Nullable;

/**
 * @author WireSegal
 * Created at 11:40 AM on 7/3/23.
 */
public interface ILootrIntegration {

	ILootrIntegration INSTANCE = Util.make(() -> {
		if (ModList.get().isLoaded("lootr")) {
			return new LootrIntegration();
		}
		return new ILootrIntegration() {
			//NO-OP
		};
	});

	default BlockEntityType<? extends ChestBlockEntity> chestTE() {
		return null;
	}

	default BlockEntityType<? extends ChestBlockEntity> trappedChestTE() {
		return null;
	}

	@Nullable
	default Block lootrVariant(Block base) {
		return null;
	}

	default void postRegister() {
		// NO-OP
	}

	default void loadComplete() {
		// NO-OP
	}

	@OnlyIn(Dist.CLIENT)
	default void clientSetup() {
		// NO-OP
	}

	@OnlyIn(Dist.CLIENT)
	default void stitch(ZPreTextureStitch event) {

	}
}
