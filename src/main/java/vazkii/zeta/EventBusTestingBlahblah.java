package vazkii.zeta;

import vazkii.zeta.event.IZetaEvent;
import vazkii.zeta.event.ZetaEventBus;
import vazkii.zeta.event.ZetaSubscribe;

@SuppressWarnings("PMD.SystemPrintln")
public class EventBusTestingBlahblah {
	public static class MyEvent implements IZetaEvent {
		public MyEvent setMessage(String asd) {
			message = asd;
			return this;
		}

		String message = "hello world";
	}

	public static class MySubclass extends MyEvent {

	}

	public static class Subcribe {
		String me = "goodbbye world";

		@ZetaSubscribe
		public void doIt(MyEvent e) {
			System.out.println("ME: " + me);
			System.out.println("MSG: " + e.message);
			System.out.println("EVENT TYPE: " + e.getClass());
			System.out.println();
		}

		@ZetaSubscribe
		public static void myStatic(MyEvent e) {
			System.out.println("im static!");
			System.out.println("MSG: " + e.message);
			System.out.println("EVENT TYPE: " + e.getClass());
			System.out.println();
		}
	}

	public static class Wrong {
		@ZetaSubscribe
		private void blah(MyEvent what) {

		}
	}

	public static void main(String... args) {
		ZetaEventBus imagineABus = new ZetaEventBus();

		Subcribe subscruber = new Subcribe();
		imagineABus.register(subscruber);
		imagineABus.register(Subcribe.class);

		imagineABus.fire(new MyEvent());
		imagineABus.fire(new MyEvent().setMessage("MESSAGE"));
		imagineABus.fire(new MySubclass());
		imagineABus.fire(new MySubclass().setMessage("MAESSAGE"));

		System.out.println("yeah");

		imagineABus.register(new Wrong());
	}
}
