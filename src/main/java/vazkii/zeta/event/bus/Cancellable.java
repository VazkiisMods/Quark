package vazkii.zeta.event.bus;

public interface Cancellable {
	boolean isCanceled();
	void setCanceled(boolean cancel);

	default void cancel() {
		setCanceled(true);
	}
}
