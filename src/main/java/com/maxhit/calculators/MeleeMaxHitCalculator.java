package com.maxhit.calculators;

import com.maxhit.NextMaxHitReqs;
import com.maxhit.PrayerType;
import com.maxhit.styles.AttackStyle;
import lombok.extern.slf4j.Slf4j;
import com.maxhit.sets.DharokSet;
import com.maxhit.sets.ObsidianSet;
import com.maxhit.styles.CombatStyle;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Skill;
import net.runelite.client.game.ItemEquipmentStats;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;

@Slf4j
public class MeleeMaxHitCalculator extends MaxHitCalculator
{

	private final DharokSet dharokSetChecker;
	private final ObsidianSet obsidianSetChecker;
	private double baseDamage;
	private double specialBonus;

	protected MeleeMaxHitCalculator(Client client, ItemManager itemManager, AttackStyle attackStyle)
	{
		super(client, itemManager, Skill.STRENGTH, attackStyle);
		dharokSetChecker = new DharokSet(client);
		obsidianSetChecker = new ObsidianSet(client);
		reset();
	}

	@Override
	protected void reset()
	{
		super.reset();
		baseDamage = 0.0;
		specialBonus = 1.0;
	}

	private void getStyleBonus()
	{
		if (attackStyle == AttackStyle.AGGRESSIVE) {styleBonus = 3.0;}
		if (attackStyle == AttackStyle.CONTROLLED) {styleBonus = 1.0;}
	}


	private void getVoidModifier()
	{
		if (voidSetChecker.isWearingVoid(CombatStyle.MELEE))
		{
			voidBonus = 1.1;
		}
		if (eliteVoidSetChecker.isWearingEliteVoid(CombatStyle.MELEE))
		{
			voidBonus = 1.1;
		}
	}

	@Override
	protected void getPrayerBonus()
	{
		if (PrayerType.BURST_OF_STRENGTH.isActive(client))
		{
			prayerBonus = 1.05;
		}
		if (PrayerType.SUPERHUMAN_STRENGTH.isActive(client))
		{
			prayerBonus = 1.1;
		}
		if (PrayerType.ULTIMATE_STRENGTH.isActive(client))
		{
			prayerBonus = 1.15;
		}
		if (PrayerType.CHIVALRY.isActive(client))
		{
			prayerBonus = 1.18;
		}
		if (PrayerType.PIETY.isActive(client))
		{
			prayerBonus = 1.23;
		}
	}

	@Override
	protected void getEffectiveStrength()
	{
		getStyleBonus();
		getPrayerBonus();
		getVoidModifier();
		effectiveStrength = Math.floor((Math.floor(getSkillLevel() * prayerBonus) + styleBonus + 8.0) * voidBonus);
	}

	@Override
	protected void getStrengthBonus()
	{
		if (equippedItems == null)
		{
			return;
		}
		int bonus = 0;
		//get str bonus of worn equipment
		for (EquipmentInventorySlot slot : EquipmentInventorySlot.values())
		{
			// Have to convert enum to int i.e. use ordinal
			Item item = equippedItems.getItem(slot.getSlotIdx());
			if (item == null)
			{
				continue;
			}
			int id = item.getId();
			final ItemStats stats = itemManager.getItemStats(id);
			if (stats == null)
			{
				continue;
			}
			final ItemEquipmentStats itemStats = stats.getEquipment();
			bonus += itemStats.getStr();
		}
		strengthBonus = bonus;
	}

	private void getBaseDamage()
	{
		getEffectiveStrength();
		getStrengthBonus();
		double bonusMultiplier = (strengthBonus + 64.0) / 640.0;
		baseDamage = Math.floor(0.5 + (effectiveStrength * bonusMultiplier));
	}

	private void getSpecialBonus()
	{
		//Melee sets
		// Excludes special attacks
		if (dharokSetChecker.isWearingSet())
		{
			double baseHitpoints = client.getRealSkillLevel(Skill.HITPOINTS);
			double currentHitpoints = client.getBoostedSkillLevel(Skill.HITPOINTS);
			specialBonus += (1 + (((baseHitpoints - currentHitpoints) / 100) * baseHitpoints / 100));
		}

		if (obsidianSetChecker.isWearingMaxSet())
		{
			specialBonus += 0.32;
		}
		if (obsidianSetChecker.isWearingWeaponAndNecklace())
		{
			specialBonus += 0.2;
		}
		if (obsidianSetChecker.isWearingSet())
		{
			specialBonus += 0.1;
		}
		getSalveBonus();
		specialBonus += salveBonus;
	}

	//TODO add support for Keris/Keris Partisan vs Kalphites

	@Override
	public void CalculateMaxHit()
	{
		reset();
		getSpecialBonus();
		getBaseDamage();
		maxHit = Math.floor(baseDamage * specialBonus);
		calculateNextMaxHitReqs();
	}

	@Override
	protected void calculateNextMaxHitReqs()
	{
		final double nextMaxHit = maxHit + 1.0;
		double nextBaseDamage = nextMaxHit / specialBonus;

		// Calculate needed strength bonus
		final double requiredStrengthBonus = ((nextBaseDamage - 0.5) * 640 / effectiveStrength) - 64;
		final double requiredEffectiveStrength = ((nextBaseDamage - 0.5) * 640) / (strengthBonus + 64);
		final double requiredLevel = (Math.ceil((Math.ceil(requiredEffectiveStrength) / voidBonus) - styleBonus - 8.0))  / prayerBonus;
		final double requiredPrayer = (Math.ceil((Math.ceil(requiredEffectiveStrength) / voidBonus) - styleBonus - 8.0)) / getSkillLevel();

		final double levelDiff = Math.ceil(requiredLevel - getSkillLevel());
		final double strengthBonusDiff = Math.ceil(requiredStrengthBonus - strengthBonus);
		final double prayerDiff = Math.ceil((requiredPrayer - prayerBonus) * 100.0);

		nextMaxHitReqs = new NextMaxHitReqs(skill, levelDiff, strengthBonusDiff, prayerDiff);
	}
}
