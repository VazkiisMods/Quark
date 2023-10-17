package vazkii.zeta.event.bus;

public interface Cancellable {
	boolean isCancelled();
	void cancel();
}
