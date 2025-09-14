package com.maxhit.sets;

//Used for determining if the user is wearing a set

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.api.Item;


public abstract class EquipmentSet
{
	protected Client client;

	@Setter
	protected static ItemContainer equippedItems;

	protected abstract Map<EquipmentInventorySlot, Collection<Integer>> getEquipment();

	public abstract double getMultiplier();

	public boolean isWearingSet()
	{
		if (equippedItems == null)
		{
			return false;
		}

		for (Map.Entry<EquipmentInventorySlot, Collection<Integer>> requirement : getEquipment().entrySet())
		{
 			final EquipmentInventorySlot slot = requirement.getKey();
			final Collection<Integer> validItemIds = requirement.getValue();

			final Item equippedItem = equippedItems.getItem(slot.getSlotIdx());

			if (equippedItem == null || !validItemIds.contains(equippedItem.getId()))
			{
				return false;
			}
		}

		return true;
	}

}
