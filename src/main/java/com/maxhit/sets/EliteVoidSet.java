package com.maxhit.sets;

import com.maxhit.styles.CombatStyle;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemVariationMapping;

public class EliteVoidSet extends VoidSet
{
	public EliteVoidSet(Client client, Skill skill)
	{
		super(client, skill);
	}

	@Override
	public Map<EquipmentInventorySlot, Collection<Integer>> getEquipment()
	{
		return Map.of(
			EquipmentInventorySlot.HEAD, getHelmIds(),
			EquipmentInventorySlot.BODY, ItemVariationMapping.getVariations(ItemID.ELITE_VOID_KNIGHT_TOP),
			EquipmentInventorySlot.LEGS, ItemVariationMapping.getVariations(ItemID.ELITE_VOID_KNIGHT_ROBES),
			EquipmentInventorySlot.GLOVES, ItemVariationMapping.getVariations(ItemID.PEST_VOID_KNIGHT_GLOVES)
		);
	}

	@Override
	public double getMultiplier()
	{
		switch (skill)
		{
			case STRENGTH:
				return 1.1;
			case RANGED:
				return 1.125;
			case MAGIC:
				return 1.05;
		}
		return 1.0;
	}
}
