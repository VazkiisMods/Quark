package org.violetmoon.quark.integration.lootr.client;

import org.violetmoon.zeta.client.event.load.ZClientSetup;

public interface IClientLootrIntegration {

	default void clientSetup(ZClientSetup event) {

	}

	class Dummy implements IClientLootrIntegration {
	}

}
