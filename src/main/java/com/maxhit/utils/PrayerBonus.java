package com.maxhit.utils;

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
        if (client.isPrayerActive(Prayer.BURST_OF_STRENGTH)) return 1.05;
        if (client.isPrayerActive(Prayer.SUPERHUMAN_STRENGTH)) return 1.10;
        if (client.isPrayerActive(Prayer.ULTIMATE_STRENGTH)) return 1.15;
        if (client.isPrayerActive(Prayer.CHIVALRY)) return 1.18;
        if (client.isPrayerActive(Prayer.PIETY)) return 1.23;
        return 1.0;
    }

    public static double getRangedPrayerBonus(Client client) {
        //Ranged prayers
        if (client.isPrayerActive(Prayer.SHARP_EYE)) { return 1.05; }
        if (client.isPrayerActive(Prayer.HAWK_EYE)) { return 1.1; }
        if (client.isPrayerActive(Prayer.EAGLE_EYE)) { return 1.15; }
        if (client.isPrayerActive(Prayer.RIGOUR)) { return 1.23; }
        return 1;
    }

    public double getMaxMeleePrayerBonus(int prayerLevel, int defenseLevel) {
        if (prayerLevel < 13) return 1.05; // Burst of Strength
        if (prayerLevel < 31) return 1.1; // Superhuman Strength
        if (prayerLevel < 60) return 1.15; // Ultimate Strength
        if (prayerLevel < 70 && defenseLevel >= 65) return 1.18; // Chivalry
        if (prayerLevel >= 70 && defenseLevel >= 70) return 1.23; // Piety
        return 1;
    }

    public double getMaxRangedPrayerBonus(int prayerLevel, int defenseLevel) {
        if (prayerLevel < 8) return 1.05; // Sharp Eye
        if (prayerLevel < 26) return 1.1; // Hawk Eye
        if (prayerLevel < 74) return 1.15; // Eagle Eye
        if (defenseLevel >= 70) return 1.23; // Rigour
        return 1;
    }
}
