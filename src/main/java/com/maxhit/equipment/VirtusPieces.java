package com.maxhit.equipment;

import java.util.Collection;
import lombok.AllArgsConstructor;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemVariationMapping;

@AllArgsConstructor
public enum VirtusPieces
{
	VIRTUS_MASK(ItemVariationMapping.getVariations(ItemID.VIRTUS_MASK), EquipmentInventorySlot.HEAD),
	VIRTUS_ROBE_TOP(ItemVariationMapping.getVariations(ItemID.VIRTUS_TOP), EquipmentInventorySlot.BODY),
	VIRTUS_ROBE_BOTTOM(ItemVariationMapping.getVariations(ItemID.VIRTUS_LEGS), EquipmentInventorySlot.LEGS),
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
