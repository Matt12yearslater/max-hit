package com.maxhit.sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.gameval.ItemID;

public class ObsidianSet extends EquipmentSet
{

	public ObsidianSet(Client client)
	{
		this.client = client;
	}

	@Override
	public Map<EquipmentInventorySlot, Collection<Integer>> getEquipment()
	{
		return Map.of(
			EquipmentInventorySlot.HEAD, Set.of(ItemID.OBSIDIAN_HELMET),
			EquipmentInventorySlot.BODY, Set.of(ItemID.OBSIDIAN_PLATEBODY),
			EquipmentInventorySlot.LEGS, Set.of(ItemID.OBSIDIAN_PLATELEGS),
			EquipmentInventorySlot.WEAPON, Set.of(
				ItemID.TZHAAR_SPLITSWORD,
				ItemID.BR_OBSIDIAN_SWORD,
				ItemID.TZHAAR_KNIFE,
				ItemID.TZHAAR_MACE,
				ItemID.TZHAAR_MAUL,
				ItemID.TZHAAR_MAUL_T
			)
			);
	}

	@Override
	public double getMultiplier()
	{
		return 1.1;
	}
}
