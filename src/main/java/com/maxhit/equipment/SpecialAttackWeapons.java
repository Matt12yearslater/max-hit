package com.maxhit.equipment;

import net.runelite.api.gameval.ItemID;

import java.util.HashMap;
import java.util.Map;

public class SpecialAttackWeapons {

    public static final Map<Integer, Double> WeaponMap = new HashMap<Integer, Double>()
    {{
        put(ItemID.ABYSSAL_DAGGER, 0.85);
        put(ItemID.DRAGON_CLAWS, 1.98);
        put(ItemID.DRAGON_HALBERD, 1.1);
        put(ItemID.DRAGON_DAGGER, 1.15);
        put(ItemID.DRAGON_LONGSWORD, 1.25);
        put(ItemID.DRAGON_MACE, 1.5);
        put(ItemID.DRAGON_SHORTSWORD, 1.25);
        put(ItemID.DRAGON_WARHAMMER, 1.5);
        put(ItemID.AGS, 1.375);
        put(ItemID.BGS, 1.21);
        put(ItemID.SGS, 1.1);
        put(ItemID.SARADOMIN_SWORD, 1.1);
        put(ItemID.BLESSED_SARADOMIN_SWORD, 1.25);
        put(ItemID.ZGS, 1.1);
        put(ItemID.BRAIN_ANCHOR, 1.1);
        put(ItemID.CRYSTAL_HALBERD, 1.1);
        put(ItemID.LIGHT_BALLISTA, 1.25);
        put(ItemID.HEAVY_BALLISTA, 1.25);
        put(ItemID.XBOWS_CROSSBOW_DRAGON, 1.2);
        put(ItemID.DARKBOW, 1.3);
        put(ItemID.TOXIC_BLOWPIPE, 1.5);
    }};

    public static double GetWeaponSpecDamageMultiplier(int weaponId)
    {
        if (WeaponMap.containsKey(weaponId))
            return WeaponMap.get(weaponId);
        return -1;
    }
}