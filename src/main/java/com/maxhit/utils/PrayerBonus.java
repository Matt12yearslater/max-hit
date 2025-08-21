package com.maxhit.utils;

import com.maxhit.PrayerType;
import com.maxhit.styles.CombatStyle;
import net.runelite.api.Client;
import net.runelite.api.Prayer;

public class PrayerBonus {

    public static double getPrayerBonus(Client client, CombatStyle combatStyle)
    {
        switch (combatStyle)
        {
            case MELEE:
                return getMeleePrayerBonus(client);
            case RANGED:
                return getRangedPrayerBonus(client);
        }
        return 1.0;
    }

    public static double getMeleePrayerBonus(Client client) {
        //Melee prayers
        if (PrayerType.BURST_OF_STRENGTH.isActive(client)) return 1.05;
        if (PrayerType.SUPERHUMAN_STRENGTH.isActive(client)) return 1.10;
        if (PrayerType.ULTIMATE_STRENGTH.isActive(client)) return 1.15;
        if (PrayerType.CHIVALRY.isActive(client)) return 1.18;
        if (PrayerType.PIETY.isActive(client)) return 1.23;
        return 1.0;
    }

    public static double getRangedPrayerBonus(Client client) {
        //Ranged prayers
        if (PrayerType.SHARP_EYE.isActive(client)) { return 1.05; }
        if (PrayerType.HAWK_EYE.isActive(client)) { return 1.1; }
        if (PrayerType.EAGLE_EYE.isActive(client)) { return 1.15; }
        if (PrayerType.RIGOUR.isActive(client)) { return 1.23; }
        return 1.0;
    }

    public double getMaxMeleePrayerBonus(int prayerLevel, int defenseLevel) {
        if (prayerLevel < 13) return 1.05; // Burst of Strength
        if (prayerLevel < 31) return 1.1; // Superhuman Strength
        if (prayerLevel < 60) return 1.15; // Ultimate Strength
        if (prayerLevel < 70 && defenseLevel >= 65) return 1.18; // Chivalry
        if (prayerLevel >= 70 && defenseLevel >= 70) return 1.23; // Piety
        return 1.0;
    }

    public double getMaxRangedPrayerBonus(int prayerLevel, int defenseLevel) {
        if (prayerLevel < 8) return 1.05; // Sharp Eye
        if (prayerLevel < 26) return 1.1; // Hawk Eye
        if (prayerLevel < 74) return 1.15; // Eagle Eye
        if (defenseLevel >= 70) return 1.23; // Rigour
        return 1.0;
    }
}
