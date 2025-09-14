package com.maxhit.sets;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemVariationMapping;

public class DharokSet extends EquipmentSet
{
	public DharokSet(Client client)
	{
		this.client = client;
	}

	@Override
	public Map<EquipmentInventorySlot, Collection<Integer>> getEquipment()
	{
		return Map.of(
			EquipmentInventorySlot.HEAD, ItemVariationMapping.getVariations(ItemID.BARROWS_DHAROK_HEAD),
			EquipmentInventorySlot.BODY, ItemVariationMapping.getVariations(ItemID.BARROWS_DHAROK_BODY),
			EquipmentInventorySlot.LEGS, ItemVariationMapping.getVariations(ItemID.BARROWS_DHAROK_LEGS),
			EquipmentInventorySlot.WEAPON, ItemVariationMapping.getVariations(ItemID.BARROWS_DHAROK_WEAPON)
		);
	}

	@Override
	public double getMultiplier()
	{
		double baseHitpoints = client.getRealSkillLevel(Skill.HITPOINTS);
		double currentHitpoints = client.getBoostedSkillLevel(Skill.HITPOINTS);
		return (1.0 + (((baseHitpoints - currentHitpoints) / 100.0) * baseHitpoints / 100.0));
	}
}
