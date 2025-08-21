package com.maxhit.equipment;

import javax.annotation.Nonnull;
import net.runelite.api.Item;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.EquipmentInventorySlot;


public class EquipmentFunctions
{

	public static boolean HasEquipped(ItemContainer equippedItems, EquipmentInventorySlot slot, int itemID)
	{
		if (equippedItems == null)
		{
			return false;
		}
		Item equippedItem = equippedItems.getItem(slot.ordinal());
		if (equippedItem == null)
		{
			return false;
		}
		return equippedItem.getId() == itemID;
	}

	@Nonnull
	public static String GetEquippedItemString(Client client, ItemContainer equippedItems, EquipmentInventorySlot slot)
	{
		if (equippedItems == null)
		{
			return "";
		}
		Item equippedItem = equippedItems.getItem(slot.getSlotIdx());
		if (equippedItem == null)
		{
			return "";
		}
		return client.getItemDefinition(equippedItem.getId()).getName();
	}
}
