package com.maxhit.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.api.Client;


@AllArgsConstructor
public enum EnchantedBolt
{
	DIAMOND("Diamond", 1.15),
	ONYX("Onyx", 1.2),
	DRAGONSTONE("Dragonstone", -1.0)
	{
		@Override
		public double getDamageMultiplier(double baseMaxHit, int rangedLevel)
		{
			return baseMaxHit + Math.floor(rangedLevel * 0.2);
		}
	},
	OPAL("Opal", -1.0)
	{
		@Override
		public double getDamageMultiplier(double baseMaxHit, int rangedLevel)
		{
			return Math.floor(baseMaxHit) + Math.floor(rangedLevel * 0.1);
		}
	}
	//TODO add logic for Pearl Bolts
	;
	@Getter
	private final String name;
	private final double damageMultiplier;

	public double getDamageMultiplier(double baseMaxHit, int rangedLevel)
	{
		return Math.floor(baseMaxHit * damageMultiplier);
	}

	public boolean isEquipped(Client client, ItemContainer equippedItems)
	{
		return EquipmentFunctions.GetEquippedItemString(client, equippedItems, EquipmentInventorySlot.AMMO).contains(name);
	}
}
