package com.maxhit.equipment;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import lombok.AllArgsConstructor;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemVariationMapping;

@FunctionalInterface
interface Calculation
{
	double compute(int skillLevel);
}

@AllArgsConstructor
public enum PoweredStaff
{
	WARPED_SCEPTRE(ItemID.WARPED_SCEPTRE_UNCHARGED,
		(skillLevel) -> Math.floor(((8.0 * skillLevel + 96.0) / 37.0))),
	BONE_STAFF(ItemID.RAT_BONE_STAFF,
		(skillLevel) -> Math.floor((skillLevel / 3.0)) + 5.0),
	TRIDENT_OF_THE_SEAS(ItemID.TOTS,
		(skillLevel) -> Math.max(1.0, Math.floor((skillLevel / 3.0)) - 5.0)),
	THAMMARONS_SCEPTRE(ItemID.WILD_CAVE_SCEPTRE_UNCHARGED,
		(skillLevel) -> Math.floor((skillLevel / 3.0)) - 8.0),
	ACCURSED_SCEPTRE(ItemID.WILD_CAVE_ACCURSED_UNCHARGED,
		(skillLevel) -> Math.floor((skillLevel / 3.0)) - 6.0),
	TRIDENT_OF_THE_SWAMP(ItemID.TOXIC_TOTS_CHARGED,
		(skillLevel) -> Math.max(4.0, Math.floor((skillLevel / 3.0)) - 2.0)),
	DAWNBRINGER(ItemID.VERZIK_SPECIAL_WEAPON,
		(skillLevel) -> Math.max(2.0, Math.floor(Math.floor((skillLevel / 3.0)) - 2.0) / 2.0)),
	SANGUINESTI_STAFF(ItemID.SANGUINESTI_STAFF,
	(skillLevel) -> Math.max(5.0, Math.floor((skillLevel / 3.0) - 1.0))),
	SANGUINESTI_STAFF_OR(ItemID.SANGUINESTI_STAFF_OR,
		(skillLevel) -> Math.max(5.0, Math.floor((skillLevel / 3.0) - 1.0))),
	EYE_OF_AYAK(ItemID.EYE_OF_AYAK,
		(skillLevel) -> Math.floor((skillLevel / 3.0)) - 6.0),
	TUMEKENS_SHADOW(ItemID.TUMEKENS_SHADOW,
		(skillLevel) -> Math.max(1.0, Math.floor((skillLevel / 3.0) + 1.0))),
	CRYSTAL_STAFF_BASIC(ItemID.GAUNTLET_MAGIC_T1,
		(skillLevel) -> 23.0),
	CRYSTAL_STAFF_ATTUNED(ItemID.GAUNTLET_MAGIC_T2,
		(skillLevel) -> 31.0),
	CRYSTAL_STAFF_PERFECTED(ItemID.GAUNTLET_MAGIC_T3,
		(skillLevel) -> 39.0),
	;



	private final int itemId;
	private final Calculation baseMaxHitCalculation;
	public double getBaseMaxHit(int magicLevel)
	{
		return Math.max(0.0, baseMaxHitCalculation.compute(magicLevel));
	}

	public Collection<Integer> getVarients()
	{
		int baseId = ItemVariationMapping.map(itemId);
		return ItemVariationMapping.getVariations(baseId);
	}
}
