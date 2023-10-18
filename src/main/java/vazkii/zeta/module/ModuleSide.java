package vazkii.zeta.module;

import vazkii.zeta.util.ZetaSide;

public enum ModuleSide {
	ANY,
	CLIENT_ONLY,
	SERVER_ONLY; //Dedicated server only!

	public boolean appliesTo(ZetaSide side) {
		return switch(this) {
			case ANY -> true;
			case CLIENT_ONLY -> side == ZetaSide.CLIENT;
			case SERVER_ONLY -> side == ZetaSide.SERVER;
		};
	}
}
