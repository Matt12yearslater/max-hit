package com.maxhit.calculators;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.client.game.ItemEquipmentStats;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StrengthBonusCalculator
{

	public static float getStrengthBonus(ItemContainer equippedItems, ItemManager itemManager, Skill skill)
	{
		float strengthBonus = 0.0f;
		if (equippedItems == null)
		{
			return strengthBonus;
		}
		//get str bonus of worn equipment
		for (EquipmentInventorySlot slot : EquipmentInventorySlot.values())
		{
			// Have to convert enum to int i.e. use ordinal
			Item item = equippedItems.getItem(slot.getSlotIdx());
			if (item == null)
			{
				continue;
			}
			int itemId = item.getId();
			final ItemStats itemStats = itemManager.getItemStats(itemId);
			if (itemStats == null)
			{
				continue;
			}
			final ItemEquipmentStats itemEquipmentStats = itemStats.getEquipment();
			strengthBonus += getItemStrength(skill, itemEquipmentStats);
		}
		return strengthBonus;
	}

	private static float getItemStrength(Skill skill, ItemEquipmentStats itemEquipmentStats)
	{
		if (skill == Skill.STRENGTH)
			return (float) itemEquipmentStats.getStr();
		if (skill == Skill.RANGED)
			return (float) itemEquipmentStats.getRstr();
		return itemEquipmentStats.getMdmg();
	}
}
