package com.maxhit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.gameval.VarbitID;

// Based off of https://github.com/runelite/runelite/blob/master/runelite-client/src/main/java/net/runelite/client/plugins/prayer/PrayerType.java
@AllArgsConstructor
@Getter
public enum PrayerType
{
    // Melee
    BURST_OF_STRENGTH("Burst of Strength", Prayer.BURST_OF_STRENGTH),
    SUPERHUMAN_STRENGTH("Superhuman Strength", Prayer.SUPERHUMAN_STRENGTH),
    ULTIMATE_STRENGTH("Ultimate Strength", Prayer.ULTIMATE_STRENGTH),
    CHIVALRY("Chivalry", Prayer.CHIVALRY),
    PIETY("Piety", Prayer.PIETY),

    //Ranged
    SHARP_EYE("Sharp Eye", Prayer.SHARP_EYE),
    HAWK_EYE("Hawk Eye", Prayer.HAWK_EYE),
    EAGLE_EYE("Eagle Eye", Prayer.EAGLE_EYE)
            {
                @Override
                public boolean isEnabled(Client client)
                {
                    return !DEADEYE.isEnabled(client);
                }
            },
    DEADEYE("Deadeye", Prayer.DEADEYE)
            {
                @Override
                public boolean isEnabled(Client client)
                {
                    boolean inLms = client.getVarbitValue(VarbitID.BR_INGAME) != 0;
                    boolean deadeye = client.getVarbitValue(VarbitID.PRAYER_DEADEYE_UNLOCKED) != 0;
                    return deadeye && !inLms;
                }
            },
    RIGOUR("Rigour", Prayer.RIGOUR),

    //Magic
    MYSTIC_WILL("Mystic Will", Prayer.MYSTIC_WILL),
    MYSTIC_LORE("Mystic Lore", Prayer.MYSTIC_LORE),
    MYSTIC_MIGHT("Mystic Might", Prayer.MYSTIC_MIGHT)
            {
                @Override
                public boolean isEnabled(Client client)
                {
                    return !MYSTIC_VIGOUR.isEnabled(client);
                }
            },
    MYSTIC_VIGOUR("Mystic Vigour", Prayer.MYSTIC_VIGOUR)
            {
                @Override
                public boolean isEnabled(Client client)
                {
                    boolean inLms = client.getVarbitValue(VarbitID.BR_INGAME) != 0;
                    boolean vigour = client.getVarbitValue(VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED) != 0;
                    return vigour && !inLms;
                }
            },
    AUGURY("Augury", Prayer.AUGURY),

    // Leagues
    RP_ANCIENT_STRENGTH("Ancient Strength", Prayer.RP_ANCIENT_STRENGTH),
    RP_ANCIENT_SIGHT("Ancient Sight", Prayer.RP_ANCIENT_SIGHT),
    RP_TRINITAS("Trinitas", Prayer.RP_TRINITAS),
    RP_DECIMATE("Decimate", Prayer.RP_DECIMATE),
    RP_ANNIHILATE("Annihilate", Prayer.RP_ANNIHILATE),
    RP_VAPORISE("Vaporise", Prayer.RP_VAPORISE),
    ;

    private final String name;
    private final Prayer prayer;

    public boolean isEnabled(Client client)
    {
        return true;
    }

    public boolean isActive(Client client)
    {
        return client.isPrayerActive(prayer) && isEnabled(client);
    }
}
