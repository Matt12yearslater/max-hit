package com.maxhit.sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemVariationMapping;

public class FullObsidianSet extends ObsidianSet
{
	public FullObsidianSet(Client client)
	{
		super(client);
	}

	@Override
	public Map<EquipmentInventorySlot, Collection<Integer>> getEquipment()
	{
		Map<EquipmentInventorySlot, Collection<Integer>> equipment = new java.util.HashMap<>(super.getEquipment());
		equipment.put(EquipmentInventorySlot.AMULET, ItemVariationMapping.getVariations(ItemID.JEWL_BESERKER_NECKLACE));
		return equipment;
	}
}
