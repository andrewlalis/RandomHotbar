package nl.andrewl.randomhotbar;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandomHotbar {
	public static void main(String[] args) throws Exception {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
			System.err.println("Could not register native hooks; exiting.");
			return;
		}

		if (args.length > 0 && args[0].equalsIgnoreCase("cycle")) {
			doCycle(args);
		} else {
			var settings = parseSlotSettings(String.join(",", args));
			var listener = new BlockPlaceListener(settings);
			GlobalScreen.addNativeMouseListener(listener);
		}
	}

	public static final Pattern SETTINGS_PATTERN = Pattern.compile("([1-9])\\s*:\\s*(\\d*\\.\\d+|\\d+)%?");

	public static List<SlotSetting> parseSlotSettings(String s) {
		List<SlotSetting> settings = new ArrayList<>(9);
		Matcher m = SETTINGS_PATTERN.matcher(s);
		while (m.find()) {
			int slot = Integer.parseInt(m.group(1));
			float value = Float.parseFloat(m.group(2));
			for (var setting : settings) {
				if (setting.slot() == slot) {
					throw new IllegalArgumentException("A setting for slot " + slot + " is already defined.");
				}
			}
			settings.add(new SlotSetting(slot, value));
		}
		settings.sort(Comparator.comparing(SlotSetting::slot));
		if (settings.isEmpty()) {
			for (int i = 1; i <= 9; i++) {
				settings.add(new SlotSetting(i, 1));
			}
		}
		return settings;
	}

	public static void doCycle(String[] args) throws Exception {
		int slots = 9;
		if (args.length > 1) {
			try {
				slots = Integer.parseUnsignedInt(args[1]);
				if (slots < 1 || slots > 9) throw new NumberFormatException("Invalid slot count.");
			} catch (NumberFormatException e) {
				System.err.println("Invalid slot count. Please specify a number from 1 to 9.");
				return;
			}
		}
		System.out.println("-".repeat(20));
		System.out.printf("Will cycle between the first %d slots.\n", slots);
		System.out.println("-".repeat(20));
		System.out.println("Select slot 1. Will begin in 3 seconds.");
		Thread.sleep(3000);
		Scroller scroller = new Scroller(1);
		Random rand = new Random();
		while (true) {
			CompletableFuture<Void> cs;
			if (scroller.getCurrentSlot() < slots) {
				cs = scroller.incrementSlot();
			} else {
				cs = scroller.moveToSlot(1);
			}
			cs.join();
			Thread.sleep(rand.nextLong(50, 250));
		}
	}
}
