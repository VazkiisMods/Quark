package vazkii.zeta;

import vazkii.zeta.event.bus.IZetaLoadEvent;
import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.ZetaEventBus;
import vazkii.zeta.event.bus.PlayEvent;

@SuppressWarnings("PMD.SystemPrintln")
public class EventBusTestingBlahblah {
	public static class MyEvent implements IZetaPlayEvent {
		public MyEvent setMessage(String asd) {
			message = asd;
			return this;
		}

		String message = "hello world";
	}

	public static class MySubclass extends MyEvent {

	}

	public static class TestLoadEvent implements IZetaLoadEvent {}
	public static class TestLoadEventSubclass extends TestLoadEvent {}

	public static class TestSubscriber {
		String me = "goodbbye world";

		@PlayEvent
		public void doIt(MyEvent e) {
			System.out.println("ME: " + me);
			System.out.println("MSG: " + e.message);
			System.out.println("EVENT TYPE: " + e.getClass());
			System.out.println();
		}

		@PlayEvent
		public static void myStatic(MyEvent e) {
			System.out.println("im static!");
			System.out.println("MSG: " + e.message);
			System.out.println("EVENT TYPE: " + e.getClass());
			System.out.println();
		}

		@LoadEvent
		public void load(TestLoadEvent e) {
			System.out.println("got LOAD EVENT ! " + this.me + " " + e);
		}
	}

	public static void main(String... args) {
		TestSubscriber subscriberA = new TestSubscriber();
		subscriberA.me = "sub a";
		TestSubscriber subscriberB = new TestSubscriber();
		subscriberB.me = "sub b";

		ZetaEventBus<IZetaLoadEvent> loadBus = new ZetaEventBus<>(LoadEvent.class, IZetaLoadEvent.class, null);
		ZetaEventBus<IZetaPlayEvent> playBus = new ZetaEventBus<>(PlayEvent.class, IZetaPlayEvent.class, null);

		loadBus.subscribe(subscriberA);

		playBus.subscribe(subscriberA)
			.subscribe(subscriberB)
			.subscribe(TestSubscriber.class);

		playBus.fire(new MyEvent());
		playBus.fire(new MyEvent().setMessage("MESSAGE"));
		playBus.fire(new MySubclass());
		playBus.fire(new MySubclass().setMessage("MAESSAGE"));

		loadBus.fire(new TestLoadEventSubclass());

		playBus.unsubscribe(subscriberA);

		playBus.fire(new MySubclass().setMessage("UNSUBSCRIBED from subscriber A"));

		playBus.unsubscribe(TestSubscriber.class);

		playBus.fire(new MySubclass().setMessage("UNSUBSCRIBED from TestSubscriber"));

		playBus.unsubscribe(subscriberB);

		playBus.fire(new MySubclass().setMessage("UNSUBSCRIBED from subscriber B"));

		System.out.println("yeah");
	}
}
