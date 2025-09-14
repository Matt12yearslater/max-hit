package com.maxhit.calculators;

import com.maxhit.NextMaxHitReqs;
import com.maxhit.PrayerType;
import com.maxhit.styles.AttackStyle;
import com.maxhit.styles.CombatStyle;
import java.util.Collection;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.game.ItemManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.game.ItemVariationMapping;

@Slf4j
public class RangedMaxHitCalculator extends MaxHitCalculator
{
	private double baseDamage;
	private double gearBonus;
	private double specialBonus;

	protected RangedMaxHitCalculator(Client client, ItemManager itemManager, AttackStyle attackStyle)
	{
		super(client, itemManager, Skill.RANGED, attackStyle);
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

	@Override
	protected void getStyleBonus()
	{
		// Have to use different method since ATTACK style is just equal to RANGED
		if (client.getVarpValue(VarPlayerID.COM_MODE) == AttackStyle.ACCURATE.ordinal())
		{
			styleBonus = 3.0;
		}
	}


	private void getGearBonus()
	{
		getSalveBonus();
		gearBonus += salveBonus;
		//TODO Add support for slayer, Craw's bow, and Twisted bow
	}

	private void getBaseHit()
	{
		getEffectiveStrength();
		getStrengthBonus();
		getGearBonus();
		double strengthCalculation = (effectiveStrength * (strengthBonus + 64)) / 640.0;
		double firstFloorCalculation = Math.floor(0.5 + strengthCalculation);
		baseDamage = Math.floor(firstFloorCalculation * gearBonus);
	}

	private void getSpecialBonus()
	{
		Item weaponItem = equippedItems.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
		if (weaponItem == null)
		{
			return;
		}

		if (!ItemVariationMapping.getVariations(ItemID.BOW_OF_FAERDHINEN).contains(weaponItem.getId()))
		{
			return;
		}

		Item headSlotItem = equippedItems.getItem(EquipmentInventorySlot.HEAD.getSlotIdx());
		if (headSlotItem != null && ItemVariationMapping.getVariations(ItemID.CRYSTAL_HELMET).contains(headSlotItem.getId()))
		{
			specialBonus += 0.025;
		}

		Item bodySlotItem = equippedItems.getItem(EquipmentInventorySlot.BODY.getSlotIdx());
		if (bodySlotItem != null && ItemVariationMapping.getVariations(ItemID.CRYSTAL_CHESTPLATE).contains(bodySlotItem.getId()))
		{
			specialBonus += 0.075;
		}

		Item legsSlotItem = equippedItems.getItem(EquipmentInventorySlot.LEGS.getSlotIdx());
		if (bodySlotItem != null && ItemVariationMapping.getVariations(ItemID.CRYSTAL_PLATELEGS).contains(legsSlotItem.getId()))
		{
			specialBonus += 0.05;
		}
	}

	@Override
	public void calculateMaxHit()
	{
		reset();
		getBaseHit();
		getSpecialBonus();
		//TODO add support for special attack
		maxHit = Math.floor(baseDamage * specialBonus);
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
