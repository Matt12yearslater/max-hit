package com.maxhit.sets;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemVariationMapping;

public class VoidSet extends EquipmentSet
{
	protected final Skill skill;

	public VoidSet(Client client, Skill skill)
	{
		this.client = client;
		this.skill = skill;
	}

	protected Collection<Integer> getHelmIds()
	{
		switch (skill)
		{
			case STRENGTH:
				return ItemVariationMapping.getVariations(ItemID.GAME_PEST_MELEE_HELM);
			case RANGED:
				return ItemVariationMapping.getVariations(ItemID.GAME_PEST_ARCHER_HELM);
			case MAGIC:
				return ItemVariationMapping.getVariations(ItemID.GAME_PEST_MAGE_HELM);
		}
		return null;
	}

	@Override
	public Map<EquipmentInventorySlot, Collection<Integer>> getEquipment()
	{
		return Map.of(
			EquipmentInventorySlot.HEAD, getHelmIds(),
			EquipmentInventorySlot.BODY, ItemVariationMapping.getVariations(ItemID.PEST_VOID_KNIGHT_TOP),
			EquipmentInventorySlot.LEGS, ItemVariationMapping.getVariations(ItemID.PEST_VOID_KNIGHT_ROBES),
			EquipmentInventorySlot.GLOVES, ItemVariationMapping.getVariations(ItemID.PEST_VOID_KNIGHT_GLOVES)
		);
	}

	@Override
	public double getMultiplier()
	{
		switch(skill)
		{
			case STRENGTH:
			case RANGED:
				return 1.1;
		}
		return 1.0;
	}
}
