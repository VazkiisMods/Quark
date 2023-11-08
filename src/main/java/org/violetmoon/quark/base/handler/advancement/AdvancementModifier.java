package org.violetmoon.quark.base.handler.advancement;

import com.google.common.base.Supplier;
import org.violetmoon.quark.api.IAdvancementModifier;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nullable;

public abstract class AdvancementModifier implements IAdvancementModifier {

	public final ZetaModule module;
	private Supplier<Boolean> cond;
	
	protected AdvancementModifier(@Nullable ZetaModule module) {
		this.module = module;
	}

	@Override
	public AdvancementModifier setCondition(Supplier<Boolean> cond) {
		this.cond = cond;
		return this;
	}

	@Override
	public boolean isActive() {
		return (module == null || module.enabled) && (cond == null || cond.get());
	}

}
