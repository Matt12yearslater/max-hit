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
			name = "Show Max Spec",
			description = "Show max spec in current setup",
			position = 3
	)
	default boolean showSpec() { return true; }

	@ConfigItem(
			keyName = "showMagic",
			name = "Show Magic",
			description = "Show max hit in current setup with selected spell",
			position = 4
	)
	default boolean showMagic() { return false; }


	@ConfigItem(
			keyName = "spellChoice",
			name = "Spell",
			description = "Choose spell to calculate max",
			position = 5
	)
	default MagicSpell spellChoice() { return MagicSpell.ICE_BARRAGE; }

	@ConfigItem(
			keyName = "applyCharge",
			name = "Apply Charge Spell",
			description = "Calculate max hit of spell using charge (god spells)",
			position = 6
	)
	default boolean applyCharge() { return false; }

	@ConfigItem(
			keyName = "inventoryWeapons",
			name = "Inventory Weapons' Max Hits",
			description = "Shows max hit of weapons in inventory. Assumes highest level prayer is used",
			position = 7
	)
	default boolean inventoryWeapons() { return false; }

	@ConfigItem(
			keyName = "inventoryWeaponsSpecial",
			name = "Inventory Weapons' Max Specs",
			description = "Shows max spec of weapons in inventory",
			position = 8
	)
	default boolean inventoryWeaponsSpecial() { return false; }

	@ConfigItem(
			keyName = "inventorySelectiveSpecial",
			name = "Inventory Selective Spec",
			description = "Shows spec max if weapon has spec, otherwise shows normal max. " +
					"Do not use with previous two options",
			position = 9
	)
	default boolean inventorySelectiveSpecial() { return false; }
}
