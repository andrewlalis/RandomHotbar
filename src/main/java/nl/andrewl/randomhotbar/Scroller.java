package nl.andrewl.randomhotbar;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;

import java.util.concurrent.*;

public class Scroller {
	public static final long SCROLL_DELAY_MS = 50L;

	private int currentSlot;
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	public Scroller(int currentSlot) {
		this.currentSlot = currentSlot;
	}

	public int getCurrentSlot() {
		return this.currentSlot;
	}

	public CompletableFuture<Void> incrementSlot() {
		int slot = this.currentSlot + 1;
		if (slot > 9) slot = 1;
		return moveToSlot(slot);
	}

	public CompletableFuture<Void> decrementSlot() {
		int slot = this.currentSlot - 1;
		if (slot < 1) slot = 9;
		return moveToSlot(slot);
	}

	public CompletableFuture<Void> moveToSlot(int slot) {
		int tdir = slot < currentSlot ? 1 : -1;
		int n = Math.abs(currentSlot - slot);
		if (n > 4) {
			tdir *= -1;
			n = Math.abs(9 - n);
		}
		final int dir = tdir;
		CompletableFuture<Void> cs = new CompletableFuture<>();
		for (int i = 0; i < n; i++) {
			final int iF = i;
			final int nF = n;
			executor.schedule(() -> {
				GlobalScreen.postNativeEvent(new NativeMouseWheelEvent(
						2505,
						0,
						500,
						500,
						1,
						NativeMouseWheelEvent.WHEEL_UNIT_SCROLL,
						1,
						dir,
						NativeMouseWheelEvent.WHEEL_VERTICAL_DIRECTION
				));
				if (iF == nF - 1) {
					this.currentSlot = slot;
					cs.complete(null);
				}
			}, i * SCROLL_DELAY_MS, TimeUnit.MILLISECONDS);
		}
		return cs;
	}
}
