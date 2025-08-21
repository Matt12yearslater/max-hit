package com.maxhit.calculators;

import com.maxhit.NextMaxHitReqs;
import com.maxhit.PrayerType;
import com.maxhit.styles.AttackStyle;
import com.maxhit.styles.CombatStyle;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.Skill;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.game.ItemEquipmentStats;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;

public class RangedMaxHitCalculator extends MaxHitCalculator
{
	private double baseDamage;
	private double gearBonus;
	private double specialBonus;

	protected RangedMaxHitCalculator(Client client, ItemManager itemManager, AttackStyle attackStyle)
	{
		super(client, itemManager, Skill.RANGED, attackStyle);
		salveRegularBonus = 0.0;
		salveEnchantedBonus = 0.0;
		salveImbuedBonus = 0.1667;
		salveEnchantedImbuedBonus = 0.2;
		reset();
	}

	@Override
	protected void reset()
	{
		super.reset();
		baseDamage = 0.0;
		gearBonus = 1.0;
		specialBonus = 1.0;
	}

	private void getStyleBonus()
	{
		if (client.getVarpValue(VarPlayerID.COM_MODE) == AttackStyle.ACCURATE.ordinal())
		{
			styleBonus = 3.0;
		}
	}

	@Override
	protected void getPrayerBonus()
	{
		if (PrayerType.SHARP_EYE.isActive(client))
		{
			prayerBonus = 1.05;
		}
		if (PrayerType.HAWK_EYE.isActive(client))
		{
			prayerBonus = 1.1;
		}
		if (PrayerType.EAGLE_EYE.isActive(client))
		{
			prayerBonus = 1.15;
		}
		if (PrayerType.RIGOUR.isActive(client))
		{
			prayerBonus = 1.23;
		}
	}

	private void getVoidModifier()
	{
		if (voidSetChecker.isWearingVoid(CombatStyle.RANGED))
		{
			voidBonus = 1.1;
		}
		if (eliteVoidSetChecker.isWearingEliteVoid(CombatStyle.RANGED))
		{
			voidBonus = 1.125;
		}
	}

	@Override
	protected void getEffectiveStrength()
	{
		getStyleBonus();
		getPrayerBonus();
		getVoidModifier();
		// getValue() gets boosted value
		effectiveStrength = Math.floor(Math.floor((getSkillLevel() * prayerBonus) + styleBonus + 8) * voidBonus);
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
			Item item = equippedItems.getItem(slot.ordinal());
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
			bonus += itemStats.getRstr();
		}
		strengthBonus = bonus;
	}

	private void getGearBonus()
	{
		getSalveBonus();
		gearBonus = salveBonus;
		//TODO Add support for slayer, Craw's bow, and Twisted bow
	}

	private void getBaseHit()
	{
		getEffectiveStrength();
		getStrengthBonus();
		getGearBonus();
		double strengthCalculation = (effectiveStrength * (strengthBonus + 64)) / 640.0;
		double firstFloorCalculation = Math.floor(0.5 + strengthCalculation);
		baseDamage = Math.floor(firstFloorCalculation * specialBonus);
	}

	@Override
	public void CalculateMaxHit()
	{
		reset();
		getBaseHit();
		getGearBonus();
		//TODO add support for special attack
		maxHit = Math.floor(baseDamage);
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
