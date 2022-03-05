package nl.andrewl.randomhotbar;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BlockPlaceListener implements NativeMouseInputListener {
	private final Random rand = new Random();
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private final List<SlotSetting> settings;
	private float randSum;
	private int currentSlot = 1;

	public BlockPlaceListener(List<SlotSetting> settings) {
		randSum = 0;
		for (var v : settings) {
			randSum += v.chance();
		}
		this.settings = settings;
		System.out.println("-".repeat(20));
		for (var v : settings) {
			float percent = (v.chance() / randSum) * 100.0f;
			System.out.printf("Will use slot %d, %.1f%% of the time.\n", v.slot(), percent);
		}
		System.out.println("-".repeat(20));
		System.out.printf("Starting at slot %d.\n! Please select this slot if it is not selected already, then start right-clicking.\n! Do not hold right-click.\n", currentSlot);
	}

	private int getNextSlot() {
		float idx = rand.nextFloat(randSum);
		float sum = 0;
		int i = 0;
		while (sum < idx) {
			sum += settings.get(i++).chance();
		}
		return settings.get(Math.max(0, i - 1)).slot();
	}

	private void moveToSlot(int slot) {
		final int dir = slot < currentSlot ? 1 : -1;
		int n = Math.abs(currentSlot - slot);
		for (int i = 0; i < n; i++) {
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
			}, i * 50L, TimeUnit.MILLISECONDS);
		}
		currentSlot = slot;
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent nativeEvent) {
		if (nativeEvent.getButton() == 2) {
			int newSlot = getNextSlot();
			moveToSlot(newSlot);
		}
	}
}
