package com.maxhit.equipment;

import java.util.Collection;
import lombok.AllArgsConstructor;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemVariationMapping;

@AllArgsConstructor
public enum InquisitorPieces
{
	HELM(ItemVariationMapping.getVariations(ItemID.INQUISITORS_HELM), EquipmentInventorySlot.HEAD),
	HAUBERK(ItemVariationMapping.getVariations(ItemID.INQUISITORS_BODY), EquipmentInventorySlot.BODY),
	PLATESKIRT(ItemVariationMapping.getVariations(ItemID.INQUISITORS_SKIRT), EquipmentInventorySlot.LEGS)
	;

	private final Collection<Integer> itemVariations;
	private final EquipmentInventorySlot slot;

	public boolean isEquipped(ItemContainer equippedItems)
	{
		for (int itemId : itemVariations)
		{
			if (EquipmentFunctions.HasEquipped(equippedItems, slot, itemId))
				return true;
		}
		return false;
	}
}
