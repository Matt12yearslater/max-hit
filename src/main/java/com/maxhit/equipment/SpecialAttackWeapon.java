package com.maxhit.equipment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.Client;
import net.runelite.api.gameval.VarPlayerID;

@RequiredArgsConstructor
public enum SpecialAttackWeapon
{
	ABYSSAL_BLUDGEON(ItemID.ABYSSAL_BLUDGEON, -1.0)
		{
			@Override
			public double getSpecialAttackDamage(Client client, double maxBase)
			{
				int maxPrayerPoints = client.getRealSkillLevel(Skill.PRAYER);
				int actualPrayerPoints = client.getBoostedSkillLevel(Skill.PRAYER);
				int prayerPointsMissing = maxPrayerPoints - actualPrayerPoints;
				return (1.0 + (0.005 * prayerPointsMissing));
			}
		},
	ABYSSAL_DAGGER(ItemID.ABYSSAL_DAGGER, 0.85),
	ABYSSAL_DAGGER_P(ItemID.ABYSSAL_DAGGER_P, 0.85),
	ABYSSAL_DAGGER_P_PLUS(ItemID.ABYSSAL_DAGGER_P_, 0.85),
	ABYSSAL_DAGGER_P_PLUS_PLUS(ItemID.ABYSSAL_DAGGER_P__, 0.85),
	AGS(ItemID.AGS, 1.375),
	ARKAN_BLADE(ItemID.ARKAN_BLADE, 1.5),
	BLAZING_BLOWPIPE(ItemID.TOXIC_BLOWPIPE_ORNAMENT, 1.5),
	BGS(ItemID.BGS, 1.21),
	BLESSED_SARADOMIN_SWORD(ItemID.BLESSED_SARADOMIN_SWORD, 1.25),
	BRAIN_ANCHOR(ItemID.BRAIN_ANCHOR, 1.1),
	CRYSTAL_HALBERD(ItemID.CRYSTAL_HALBERD, 1.1),
	DARKBOW(ItemID.DARKBOW, 1.3),
	DRAGON_CLAWS(ItemID.DRAGON_CLAWS, 1.98),
	DRAGON_DAGGER(ItemID.DRAGON_DAGGER, -1.0)
		{
			@Override
			public double getSpecialAttackDamage(Client client, double maxHitBase)
			{
				return 2.0 * Math.floor(maxHitBase * 1.15);
			}
		},
	DRAGON_HALBERD(ItemID.DRAGON_HALBERD, 1.1),
	DRAONG_HASTA(ItemID.BRUT_DRAGON_SPEAR, -1.0)
		{
			@Override
			public double getSpecialAttackDamage(Client client, double maxBase)
			{
				return (1.0 + (0.5 * client.getVarbitValue(VarPlayerID.SA_ENERGY) / 1000.0));
			}
		},
	DRAGON_LONGSWORD(ItemID.DRAGON_LONGSWORD, 1.25),
	DRAGON_MACE(ItemID.DRAGON_MACE, 1.5),
	DRAGON_SHORTSWORD(ItemID.DRAGON_SHORTSWORD, 1.25),
	DRAGON_WARHAMMER(ItemID.DRAGON_WARHAMMER, 1.5),
	DUAL_MACUAHUITL(ItemID.DUAL_MACUAHUITL, 1.25),
	HEAVY_BALLISTA(ItemID.HEAVY_BALLISTA, 1.25),
	LIGHT_BALLISTA(ItemID.LIGHT_BALLISTA, 1.25),
	SARADOMIN_SWORD(ItemID.SARADOMIN_SWORD, 1.1),
	SGS(ItemID.SGS, 1.1),
	TOXIC_BLOWPIPE(ItemID.TOXIC_BLOWPIPE, 1.5),
	XBOWS_CROSSBOW_DRAGON(ItemID.XBOWS_CROSSBOW_DRAGON, 1.2),
	ZGS(ItemID.ZGS, 1.1),
	;

	@Getter
	private final int itemId;

	@Getter
	private final double damageMultiplier;

	public double getSpecialAttackDamage(Client client, double maxHitBase)
	{
		return Math.floor(maxHitBase * damageMultiplier);
	}
}