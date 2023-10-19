package vazkii.quark.base.block;

import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.module.QuarkModule;

public class QuarkLeavesBlock extends LeavesBlock implements IQuarkBlock {
	
	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;
	
	public QuarkLeavesBlock(String name, QuarkModule module, MaterialColor color) {
		super(Block.Properties.of(Material.LEAVES, color)
				.strength(0.2F)
				.randomTicks()
				.sound(SoundType.GRASS)
				.noOcclusion()
				.isValidSpawn((s, r, p, t) -> false)
				.isSuffocating((s, r, p) -> false)
				.isViewBlocking((s, r, p) -> false));

		this.module = module;

		Quark.ZETA.registry.registerBlock(this, name + "_leaves", true);
		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_DECORATIONS);

		RenderLayerHandler.setRenderType(this, RenderLayerHandler.RenderTypeSkeleton.CUTOUT_MIPPED);
	}
	
	@Nullable
	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public QuarkLeavesBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

}
