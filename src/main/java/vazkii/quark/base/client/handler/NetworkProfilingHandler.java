package vazkii.quark.base.client.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.handler.GeneralConfig;

public class NetworkProfilingHandler {

	private static final Map<String, Info> map = new HashMap<>();

	public static void receive(String name) {
		if(GeneralConfig.enableNetworkProfiling) {
			if(!map.containsKey(name))
				map.put(name, new Info());
			map.get(name).add();
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void showF3(CustomizeGuiOverlayEvent.DebugText event) {
		if(GeneralConfig.enableNetworkProfiling) {
			event.getLeft().add("");

			for(String s : map.keySet()) {
				Info i = map.get(s);
				int c = i.tick();
				if(c > 0) {
					double cd = ((double) c) / 5.0;
					ChatFormatting tf = (System.currentTimeMillis() - i.getLast() < 100) ? ChatFormatting.RED : ChatFormatting.RESET;

					event.getLeft().add(tf + "PACKET " + s + ": " + cd + "/s (" + i.getCount() + ")");
				}
			}
		}
	}

	private static class Info {

		private static final List<Long> times = new ArrayList<>(100);
		int count;
		long last;

		public void add() {
			last = System.currentTimeMillis();
			count++;
			times.add(last);
		}

		public int tick() {
			long curr = System.currentTimeMillis();
			long limit = curr - 5000;
			times.removeIf(t -> t < limit);

			return times.size();
		}

		public long getLast() {
			return last;
		}

		public int getCount() {
			return count;
		}

	}

}
