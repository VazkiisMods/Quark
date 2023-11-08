package org.violetmoon.quark.base.block;

import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public class QuarkStandingSignBlock extends StandingSignBlock implements IQuarkBlock {

	private final ZetaModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkStandingSignBlock(String regname, ZetaModule module, WoodType type, Properties properties) {
		super(properties, type);
		this.module = module;

		Quark.ZETA.registry.registerBlock(this, regname, false);
	}

	@Override
	public QuarkStandingSignBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public ZetaModule getModule() {
		return module;
	}

}
