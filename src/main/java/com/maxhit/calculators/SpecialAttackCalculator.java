package com.maxhit.calculators;

import com.maxhit.equipment.EquipmentFunctions;
import com.maxhit.equipment.SpecialAttackWeapon;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;

public class SpecialAttackCalculator
{
	private Client client;
	@Setter
	private ItemContainer equippedItems;

	public SpecialAttackCalculator(Client client)
	{
		this.client = client;
	}

	public double getSpecialMaxHit(double maxHitBase)
	{
		Item weapon = equippedItems.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
		if (weapon == null)
		{
			return maxHitBase;
		}
		String weaponName = EquipmentFunctions.GetEquippedItemString(client, equippedItems, EquipmentInventorySlot.WEAPON);
		for (SpecialAttackWeapon specialAttackWeapon : SpecialAttackWeapon.values())
		{
			if (specialAttackWeapon.getItemId() == weapon.getId())
			{
				return specialAttackWeapon.getSpecialAttackDamage(client, maxHitBase);
			}
		}

		String ammoName = EquipmentFunctions.GetEquippedItemString(client, equippedItems, EquipmentInventorySlot.AMMO);
		if (weaponName.contains("rossbo") && ammoName.contains("(e)"))
		{
			int playerRangedLevel = client.getBoostedSkillLevel(Skill.RANGED);
			if (ammoName.contains("Diamond"))
			{
				return Math.floor(maxHitBase) * 1.15;
			}
			if (ammoName.contains("Onyx"))
			{
				return Math.floor(maxHitBase) * 1.2;
			}
			if (ammoName.contains("Dragonstone"))
			{
				return Math.floor(maxHitBase) + Math.floor(playerRangedLevel * 0.2);
			}
			if (ammoName.contains("Opal"))
			{
				return Math.floor(maxHitBase) + Math.floor(playerRangedLevel * 0.1);
			}
			if (ammoName.contains("Pearl"))
			{
				return Math.floor(maxHitBase) + Math.floor(playerRangedLevel * .05);
			}
		}
		return maxHitBase;
	}
}
