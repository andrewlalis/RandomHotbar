package nl.andrewl.randomhotbar;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;

import java.util.List;
import java.util.Random;

public class BlockPlaceListener implements NativeMouseInputListener {
	private final Random rand = new Random();
	private final List<SlotSetting> settings;
	private final Scroller scroller;
	private float randSum;

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
		this.scroller = new Scroller(1);
		System.out.printf("Starting at slot %d.\n! Please select this slot if it is not selected already, then start right-clicking.\n! Do not hold right-click.\n", scroller.getCurrentSlot());
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

	@Override
	public void nativeMouseClicked(NativeMouseEvent nativeEvent) {
		if (nativeEvent.getButton() == 2) {
			int newSlot = getNextSlot();
			this.scroller.moveToSlot(newSlot);
		}
	}
}
