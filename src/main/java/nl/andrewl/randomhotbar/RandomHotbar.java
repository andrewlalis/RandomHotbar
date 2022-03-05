package nl.andrewl.randomhotbar;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandomHotbar {
	public static void main(String[] args) {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}
		var settings = parseSettings(String.join(",", args));
		var listener = new BlockPlaceListener(settings);
		GlobalScreen.addNativeMouseListener(listener);
	}

	public static final Pattern SETTINGS_PATTERN = Pattern.compile("([1-9])\\s*:\\s*(\\d*\\.\\d+|\\d+)%?");

	public static List<SlotSetting> parseSettings(String s) {
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
}
