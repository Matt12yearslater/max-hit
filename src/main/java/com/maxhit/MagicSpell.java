package com.maxhit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.Skill;


@RequiredArgsConstructor
public enum MagicSpell
{
    //Most common spells
    ICE_BARRAGE(46, "Ice Barrage", 94, 30, Spellbook.ANCIENT, "ICE"),
    BLOOD_BARRAGE(45, "Blood Barrage", 92, 29, Spellbook.ANCIENT, "BLOOD"),
    SHADOW_BARRAGE(44, "Shadow Barrage", 88, 28, Spellbook.ANCIENT, "SHADOW"),
    SMOKE_BARRAGE(43, "Smoke Barrage", 86, 27, Spellbook.ANCIENT, "SMOKE"),
    ICE_BLITZ(42, "Ice Blitz", 82, 26, Spellbook.ANCIENT, "ICE"),
    BLOOD_BLITZ(41, "Blood Blitz", 80, 25, Spellbook.ANCIENT, "BLOOD"),
    SHADOW_BLITZ(40, "Shadow Blitz", 76, 24, Spellbook.ANCIENT, "SHADOW"),
    SMOKE_BLITZ(39, "Smoke Blitz", 74, 23, Spellbook.ANCIENT, "SMOKE"),
    ICE_BURST(38, "Ice Burst", 70, 22, Spellbook.ANCIENT, "ICE"),
    BLOOD_BURST(37, "Blood Burst", 68, 21, Spellbook.ANCIENT, "BLOOD"),
    SHADOW_BURST(36, "Shadow Burst", 64,18, Spellbook.ANCIENT, "SHADOW"),
    SMOKE_BURST(35, "Smoke Burst", 62, 17, Spellbook.ANCIENT, "SMOKE"),
    ICE_RUSH(34, "Ice Rush", 58, 16, Spellbook.ANCIENT, "ICE"),
    BLOOD_RUSH(33, "Blood Rush", 56, 15, Spellbook.ANCIENT, "BLOOD"),
    SHADOW_RUSH(32, "Shadow Rush", 52, 14, Spellbook.ANCIENT, "SHADOW"),
    SMOKE_RUSH(31, "Smoke Rush", 50, 13, Spellbook.ANCIENT, "SMOKE"),

    // standard spells
    FIRE_SURGE(51, "Fire Surge", 95, 24, Spellbook.STANDARD, "FIRE"),
    EARTH_SURGE(50, "Earth Surge", 90, 23, Spellbook.STANDARD, "EARTH")
		{
			@Override
			public int getBaseMaxHit(Client client)
			{
				int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
				if (magicLevel >= FIRE_SURGE.levelRequired){ return FIRE_SURGE.baseMaxHit; }
				return EARTH_SURGE.baseMaxHit;
			}
		},
    WATER_SURGE(49, "Water Surge", 85, 22, Spellbook.STANDARD, "WATER")
		{
			@Override
			public int getBaseMaxHit(Client client)
			{
				int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
				if (magicLevel >= FIRE_SURGE.levelRequired){ return FIRE_SURGE.baseMaxHit; }
				if (magicLevel >= EARTH_SURGE.levelRequired) { return EARTH_SURGE.baseMaxHit; }
				return WATER_SURGE.baseMaxHit;
			}
		},
    WIND_SURGE(48, "Wind Surge", 81, 21, Spellbook.STANDARD, "AIR")
		{
			@Override
			public int getBaseMaxHit(Client client)
			{
				int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
				if (magicLevel >= FIRE_SURGE.levelRequired){ return FIRE_SURGE.baseMaxHit; }
				if (magicLevel >= EARTH_SURGE.levelRequired) { return EARTH_SURGE.baseMaxHit; }
				if (magicLevel >= WATER_SURGE.levelRequired) { return WATER_SURGE.baseMaxHit; }
				return WIND_SURGE.baseMaxHit;
			}
		},
    FIRE_WAVE(16, "Fire Wave", 75, 20, Spellbook.STANDARD, "FIRE"),
    EARTH_WAVE(15, "Earth Wave", 70, 19, Spellbook.STANDARD, "EARTH")
		{
			@Override
			public int getBaseMaxHit(Client client)
			{
				int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
				if (magicLevel >= FIRE_WAVE.levelRequired){ return FIRE_WAVE.baseMaxHit; }
				return EARTH_WAVE.baseMaxHit;
			}
		},
    WATER_WAVE(14, "Water Wave", 65, 18, Spellbook.STANDARD, "WATER")
		{
			@Override
			public int getBaseMaxHit(Client client)
			{
				int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
				if (magicLevel >= FIRE_WAVE.levelRequired){ return FIRE_WAVE.baseMaxHit; }
				if (magicLevel >= EARTH_WAVE.levelRequired) { return EARTH_WAVE.baseMaxHit; }
				return WATER_WAVE.baseMaxHit;
			}
		},
    WIND_WAVE(13, "Wind Wave", 62, 17, Spellbook.STANDARD, "AIR")
		{
			@Override
			public int getBaseMaxHit(Client client)
			{
				int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
				if (magicLevel >= FIRE_WAVE.levelRequired){ return FIRE_WAVE.baseMaxHit; }
				if (magicLevel >= EARTH_WAVE.levelRequired) { return EARTH_WAVE.baseMaxHit; }
				if (magicLevel >= WATER_WAVE.levelRequired) { return WATER_WAVE.baseMaxHit; }
				return WIND_WAVE.baseMaxHit;
			}
		},
    FIRE_BLAST(12, "Fire Blast", 59, 16, Spellbook.STANDARD, "FIRE"),
    EARTH_BLAST(11, "Earth Blast", 53, 15, Spellbook.STANDARD, "EARTH")
		{
			@Override
			public int getBaseMaxHit(Client client)
			{
				int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
				if (magicLevel >= FIRE_BLAST.levelRequired){ return 16; }
				return EARTH_BLAST.baseMaxHit;
			}
		},
    WATER_BLAST(10, "Water Blast", 47, 14, Spellbook.STANDARD, "WATER")
		{
			@Override
			public int getBaseMaxHit(Client client)
			{
				int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
				if (magicLevel >= FIRE_BLAST.levelRequired){ return FIRE_BLAST.baseMaxHit; }
				if (magicLevel >= EARTH_BLAST.levelRequired) { return EARTH_BLAST.baseMaxHit; }
				return WATER_BLAST.baseMaxHit;
			}
		},
    WIND_BLAST(9, "Wind Blast", 41, 13, Spellbook.STANDARD, "AIR")
		{
			@Override
			public int getBaseMaxHit(Client client)
			{
				int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
				if (magicLevel >= FIRE_BLAST.levelRequired){ return FIRE_BLAST.baseMaxHit; }
				if (magicLevel >= EARTH_BLAST.levelRequired) { return EARTH_BLAST.baseMaxHit; }
				if (magicLevel >= WATER_BLAST.levelRequired) { return WATER_BLAST.baseMaxHit; }
				return WIND_BLAST.baseMaxHit;
			}
		},
    FIRE_BOLT(8, "Fire Bolt", 35, 12, Spellbook.STANDARD, "FIRE"),
    EARTH_BOLT(7, "Earth Bolt", 29, 11, Spellbook.STANDARD, "EARTH")
		{
			@Override
			public int getBaseMaxHit(Client client)
			{
				int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
				if (magicLevel >= FIRE_BOLT.levelRequired){ return FIRE_BOLT.baseMaxHit; }
				return EARTH_BOLT.baseMaxHit;
			}
		},
    WATER_BOLT(6, "Water Bolt", 23, 10, Spellbook.STANDARD, "WATER")
		{
			@Override
			public int getBaseMaxHit(Client client)
			{
				int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
				if (magicLevel >= FIRE_BOLT.levelRequired){ return FIRE_BOLT.baseMaxHit; }
				if (magicLevel >= EARTH_BOLT.levelRequired) { return EARTH_BOLT.baseMaxHit; }
				return WATER_BOLT.baseMaxHit;
			}
		},
    WIND_BOLT(5, "Wind Bolt", 17, 9, Spellbook.STANDARD, "AIR")
		{
			@Override
			public int getBaseMaxHit(Client client)
			{
				int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
				if (magicLevel >= FIRE_BOLT.levelRequired){ return FIRE_BOLT.baseMaxHit; }
				if (magicLevel >= EARTH_BOLT.levelRequired) { return EARTH_BOLT.baseMaxHit; }
				if (magicLevel >= WATER_BOLT.levelRequired) { return WATER_BOLT.baseMaxHit; }
				return WIND_BOLT.baseMaxHit;
			}
		},
    FIRE_STRIKE(4, "Fire Strike", 13, 8, Spellbook.STANDARD, "FIRE"),
    EARTH_STRIKE(3, "Earth Strike", 9, 6, Spellbook.STANDARD, "EARTH")
		{
			@Override
			public int getBaseMaxHit(Client client)
			{
				int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
				if (magicLevel >= FIRE_STRIKE.levelRequired){ return FIRE_STRIKE.baseMaxHit; }
				return 6;
			}
		},
    WATER_STRIKE(2, "Water Strike", 5, 4, Spellbook.STANDARD, "WATER")
		{
			@Override
			public int getBaseMaxHit(Client client)
			{
				int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
				if (magicLevel >= FIRE_STRIKE.levelRequired){ return FIRE_STRIKE.baseMaxHit; }
				if (magicLevel >= EARTH_STRIKE.levelRequired) { return EARTH_STRIKE.baseMaxHit; }
				return WATER_STRIKE.baseMaxHit;
			}
		},

    WIND_STRIKE(1, "Wind Strike", 1, 2, Spellbook.STANDARD, "AIR")
		{
			@Override
			public int getBaseMaxHit(Client client)
			{
				int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
				if (magicLevel >= FIRE_STRIKE.levelRequired){ return FIRE_STRIKE.baseMaxHit; }
				if (magicLevel >= EARTH_STRIKE.levelRequired) { return EARTH_STRIKE.baseMaxHit; }
				if (magicLevel >= WATER_STRIKE.levelRequired) { return WATER_STRIKE.baseMaxHit; }
				return WIND_STRIKE.baseMaxHit;
			}
		},

    // standard but not autocast without special staff
    FLAMES_OF_ZAMORAK(20, "Flames of Zamorak", 60, 20, Spellbook.STANDARD, "GOD"),
    CLAWS_OF_GUTHIX(19, "Claws of Guthix", 60, 20, Spellbook.STANDARD, "GOD"),
    SARADOMIN_STRIKE(52, "Saradomin Strike", 60, 20, Spellbook.STANDARD, "GOD"),
    CRUMBLE_UNDEAD(17, "Crumble Undead", 39, 15, Spellbook.STANDARD, ""),
    IBAN_BLAST(47, "Iban Blast", 50, 25, Spellbook.STANDARD, ""),
    MAGIC_DART(18, "Magic Dart", 50, 10, Spellbook.STANDARD, ""),

    // arceuus
    INFERIOR_DEMONBANE(53, "Inferior Demonbane", 44, 16, Spellbook.ARCEUUS, ""),
    SUPERIOR_DEMONBANE(54, "Superior Demonbane", 62, 23, Spellbook.ARCEUUS, ""),
    DARK_DEMONBANE(55, "Dark Demonbane", 82, 30, Spellbook.ARCEUUS, ""),
    GHOSTLY_GRASP(56, "Ghostly Grasp", 35, 12, Spellbook.ARCEUUS, ""),
    SKELETAL_GRASP(57, "Skeletal Grasp", 56, 17, Spellbook.ARCEUUS, ""),
    UNDEAD_GRASP(58, "Undead Grasp", 79, 24, Spellbook.ARCEUUS, ""),
    ;

	@Getter
    private final int varbValue;

	@Getter
    private final String displayName;

	@Getter
	private final int levelRequired;

    private final int baseMaxHit;

	public int getBaseMaxHit(Client client)
	{
		return baseMaxHit;
	}

	@Getter
    private final Spellbook spellbook;

	@Getter
    private final String element;

}