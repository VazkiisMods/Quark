package vazkii.zeta.event.bus;

public interface Cancellable {
	boolean isCanceled();
	void cancel();
}
