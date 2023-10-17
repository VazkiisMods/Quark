package vazkii.quark.base.block;

import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.QuarkModule;

public class QuarkWallSignBlock extends WallSignBlock implements IQuarkBlock {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkWallSignBlock(String regname, QuarkModule module, WoodType type, Properties properties) {
		super(properties, type);
		this.module = module;

		Quark.ZETA.registry.registerBlock(this, regname, false);
	}

	@Override
	public QuarkWallSignBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public QuarkModule getModule() {
		return module;
	}

}
