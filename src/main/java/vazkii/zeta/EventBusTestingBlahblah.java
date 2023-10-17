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

	public static class TestSubscriber {
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
		TestSubscriber subscriberA = new TestSubscriber();
		subscriberA.me = "sub a";
		TestSubscriber subscriberB = new TestSubscriber();
		subscriberB.me = "sub b";

		ZetaEventBus imagineABus = new ZetaEventBus();

		imagineABus.subscribe(subscriberA)
			.subscribe(subscriberB)
			.subscribe(TestSubscriber.class);

		imagineABus.fire(new MyEvent());
		imagineABus.fire(new MyEvent().setMessage("MESSAGE"));
		imagineABus.fire(new MySubclass());
		imagineABus.fire(new MySubclass().setMessage("MAESSAGE"));

		imagineABus.unsubscribe(subscriberA);

		imagineABus.fire(new MySubclass().setMessage("UNSUBSCRIBED from subscriber A"));

		imagineABus.unsubscribe(TestSubscriber.class);

		imagineABus.fire(new MySubclass().setMessage("UNSUBSCRIBED from TestSubscriber"));

		imagineABus.unsubscribe(subscriberB);

		imagineABus.fire(new MySubclass().setMessage("UNSUBSCRIBED from subscriber B"));

		System.out.println("yeah");
	}
}
