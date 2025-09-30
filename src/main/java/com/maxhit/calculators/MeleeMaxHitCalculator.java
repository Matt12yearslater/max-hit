package com.maxhit.calculators;

import com.google.common.collect.ImmutableSet;
import com.maxhit.NextMaxHitReqs;
import com.maxhit.equipment.EquipmentFunctions;
import com.maxhit.sets.FullObsidianSet;
import com.maxhit.sets.InquisitorSet;
import com.maxhit.styles.AttackStyle;
import java.util.Collection;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import com.maxhit.sets.DharokSet;
import com.maxhit.sets.ObsidianSet;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;

@Slf4j
public class MeleeMaxHitCalculator extends MaxHitCalculator
{
	private final Collection<Integer> FANGS = ItemVariationMapping.getVariations(ItemID.OSMUMTENS_FANG);
	private final Set<Integer> DINHS = ImmutableSet.of(ItemID.DINHS_BULWARK_ORNAMENT, ItemID.DINHS_BULWARK);
	private final DharokSet dharokSetChecker;
	private final ObsidianSet obsidianSetChecker;
	private final FullObsidianSet fullObsidianSetChecker;
	private double baseDamage;
	private double specialBonus;

	protected MeleeMaxHitCalculator(Client client, ItemManager itemManager, AttackStyle attackStyle)
	{
		super(client, itemManager, Skill.STRENGTH, attackStyle);
		dharokSetChecker = new DharokSet(client);
		obsidianSetChecker = new ObsidianSet(client);
		fullObsidianSetChecker = new FullObsidianSet(client);
		reset();
	}

	@Override
	protected void reset()
	{
		super.reset();
		baseDamage = 0.0;
		specialBonus = 1.0;
	}

	@Override
	protected void getStyleBonus()
	{
		if (attackStyle == AttackStyle.AGGRESSIVE) {styleBonus = 3.0;}
		if (attackStyle == AttackStyle.CONTROLLED) {styleBonus = 1.0;}
	}

	private void getBaseDamage()
	{
		getEffectiveStrength();
		getStrengthBonus();
		for (int itemId : DINHS)
		{
			if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, itemId))
			{
				strengthBonus += StrengthBonusCalculator.getDinhsBonus(attackStyle, equippedItems, itemManager);
				break;
			}
		}
		double bonusMultiplier = (strengthBonus + 64.0) / 640.0;
		baseDamage = Math.floor(0.5 + (effectiveStrength * bonusMultiplier));
	}

	private void getSpecialBonus()
	{
		//Melee sets
		// Excludes special attacks
		if (dharokSetChecker.isWearingSet())
		{
			specialBonus = dharokSetChecker.getMultiplier();
		}

		if (obsidianSetChecker.isWearingSet())
		{
			specialBonus += obsidianSetChecker.getMultiplier();
		}
		if (fullObsidianSetChecker.isWearingSet())
		{
			specialBonus += fullObsidianSetChecker.getMultiplier();
		}
		//TODO update logic to detect just amulet
		if (obsidianSetChecker.isWearingSet())
		{
			specialBonus += 0.1;
		}
		getSalveBonus();

		specialBonus += InquisitorSet.getMultiplier(client, equippedItems);

		specialBonus += salveBonus;
	}

	//TODO add support for Keris/Keris Partisan vs Kalphites

	@Override
	public void calculateMaxHit()
	{
		reset();
		getSpecialBonus();
		getBaseDamage();
		maxHit = Math.max(0.0, Math.floor(baseDamage * specialBonus));

		// The Fang calculation is a bit different. Confirmed in-game
		for(int itemId : FANGS)
		{
			if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, itemId))
			{
				double fangShrink = Math.floor(maxHit * 3.0 / 20.0);
				maxHit -= fangShrink;
				break;
			}
		}
		calculateNextMaxHitRequirements();
	}

	@Override
	protected void calculateNextMaxHitRequirements()
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
