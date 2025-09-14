package com.maxhit.sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.gameval.ItemID;

public class BerserkerNecklaceSet extends EquipmentSet
{
	@Override
	protected Map<EquipmentInventorySlot, Collection<Integer>> getEquipment()
	{
		return Map.of(EquipmentInventorySlot.AMULET, Set.of(
			ItemID.JEWL_BESERKER_NECKLACE,
			ItemID.JEWL_BESERKER_NECKLACE_ORNAMENT
		));
	}

	@Override
	public double getMultiplier()
	{
		return 0.2;
	}
}
