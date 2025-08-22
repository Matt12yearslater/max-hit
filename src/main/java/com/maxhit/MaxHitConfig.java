package com.maxhit;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("maxhit")
public interface MaxHitConfig extends Config {

	@ConfigItem(
			keyName = "maxhit",
			name = "Max Hit",
			description = "Display max hit in current setup",
			position = 1
	)
	default boolean maxHit() { return true; }

	@ConfigItem(
			keyName = "showNextMaxHit",
			name = "Calculate next max hit",
			description = "Mouse over the max hit to show the options to reach the next max hit " +
					"like strength/ranged levels, bonus or prayer boosts",
			position = 2
	)
	default boolean showNextMaxHit() { return true; }

	@ConfigItem(
			keyName = "showSpec",
			name = "Show Special Attack Max hit",
			description = "Show special attack max hit",
			position = 3
	)
	default boolean showSpec() { return true; }
}
