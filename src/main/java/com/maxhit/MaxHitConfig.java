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
			keyName = "showSpec",
			name = "Show Max Spec",
			description = "Show max spec in current setup",
			position = 2
	)
	default boolean showSpec() { return true; }

	@ConfigItem(
			keyName = "showMagic",
			name = "Show Magic",
			description = "Show max hit in current setup with selected spell",
			position = 3
	)
	default boolean showMagic() { return false; }

	@ConfigItem(
			keyName = "spellChoice",
			name = "Spell",
			description = "Choose spell to calculate max",
			position = 4
	)
	default MagicSpell spellChoice() { return MagicSpell.ICE_BARRAGE; }

	@ConfigItem(
			keyName = "applyCharge",
			name = "Apply Charge Spell",
			description = "Calculate max hit of spell using charge (god spells)",
			position = 5
	)
	default boolean applyCharge() { return false; }
}
