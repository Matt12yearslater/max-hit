package com.maxhit.calculators;

import com.maxhit.PrayerType;
import com.maxhit.equipment.SalveAmulet;
import com.maxhit.monsters.UndeadMonsters;
import com.maxhit.sets.EliteVoidSet;
import com.maxhit.sets.VoidSet;
import java.util.Map;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.Skill;

public class BonusCalculator
{
	private static final Map<PrayerType, Double> STRENGTH_PRAYERS = Map.of(
		PrayerType.PIETY, 1.23,
		PrayerType.CHIVALRY, 1.18,
		PrayerType.ULTIMATE_STRENGTH, 1.15,
		PrayerType.SUPERHUMAN_STRENGTH, 1.1,
		PrayerType.BURST_OF_STRENGTH, 1.05
	);

	private static final Map<PrayerType, Double> RANGED_PRAYERS = Map.of(
		PrayerType.RIGOUR, 1.23,
		PrayerType.DEADEYE, 1.18,
		PrayerType.EAGLE_EYE, 1.15,
		PrayerType.HAWK_EYE, 1.1,
		PrayerType.SHARP_EYE, 1.05
	);
	private static final Map<PrayerType, Double> MAGIC_PRAYERS = Map.of(
		PrayerType.AUGURY, 0.04,
		PrayerType.MYSTIC_VIGOUR, 0.03,
		PrayerType.MYSTIC_MIGHT, 0.02,
		PrayerType.MYSTIC_LORE, 0.01
	);

	private final VoidSet voidSet;
	private final EliteVoidSet eliteVoidSet;
	private final Client client;
	private final Skill skill;

	public BonusCalculator(Client client, Skill skill)
	{
		this.client = client;
		this.skill = skill;
		voidSet = new VoidSet(client, skill);
		eliteVoidSet = new EliteVoidSet(client, skill);
	}

	public double getSalveBonus(ItemContainer equippedItems, Actor opponent)
	{
		// Check if opponent is undead
		NPC npc = (NPC) opponent;

		if (npc == null)
		{
			return 0.0;
		}

		if (!UndeadMonsters.ID_LIST.contains(npc.getId()))
		{
			return 0.0;
		}

		for (SalveAmulet amuletType : SalveAmulet.values())
		{
			if (amuletType.isEquipped(equippedItems))
			{
				return amuletType.getBonus(skill);
			}
		}
		return 0.0;
	}

	public double getVoidBonus()
	{
		if (voidSet.isWearingSet())
			return voidSet.getMultiplier();

		if (eliteVoidSet.isWearingSet())
			return eliteVoidSet.getMultiplier();
		return 1.0;
	}

	public double getPrayerBonus()
	{
		switch (skill)
		{
			case STRENGTH:
				for (Map.Entry<PrayerType, Double> entry : STRENGTH_PRAYERS.entrySet())
				{
					if (entry.getKey().isActive(client))
					{
						return entry.getValue(); // Return the first active prayer found
					}
				}
				break;
			case RANGED:
				for (Map.Entry<PrayerType, Double> entry : RANGED_PRAYERS.entrySet())
				{
					if (entry.getKey().isActive(client))
					{
						return entry.getValue(); // Return the first active prayer found
					}
				}
				break;
			case MAGIC:
				for (Map.Entry<PrayerType, Double> entry : MAGIC_PRAYERS.entrySet())
				{
					if (entry.getKey().isActive(client))
					{
						return entry.getValue(); // Return the first active prayer found
					}
				}
				return 0.0;
		}
		return 1.0;
	}
}
