package vazkii.quark.base;

import vazkii.zeta.client.ClientTicker;
import vazkii.zeta.client.ZetaClient;
import vazkii.zetaimplforge.client.ForgeZetaClient;

//ngl this is mainly a place to hold ZetaClient... which has side effect in constructor
//this is kludgy, i dont like it
public class QuarkClient {
	public static QuarkClient instance;

	public static final ZetaClient ZETA_CLIENT = new ForgeZetaClient(Quark.ZETA);
	public static final ClientTicker ticker = ZETA_CLIENT.ticker; //more convenient to access

	public static void start() {
		instance = new QuarkClient();
	}
}
